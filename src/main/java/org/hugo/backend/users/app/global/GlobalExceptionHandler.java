package org.hugo.backend.users.app.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.MappingException;
import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.exceptions.user.*;
import org.hugo.backend.users.app.utils.ResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler{
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleValidationException(MethodArgumentNotValidException ex, HandlerMethod handlerMethod){
        // Obtener información sobre el método controlador que se está invocando
        RequestMethod requestMethod = handlerMethod.getMethodAnnotation(RequestMapping.class).method()[0];
        // Obtener el nombre de la clase del controlador
        Class<?> controllerClass = handlerMethod.getBeanType();
        String controllerName = controllerClass.getSimpleName();
        logger.info("Nombre del controlador: " + controllerName);
        BindingResult result = ex.getBindingResult();
        ResponseError responseError = new ResponseError();
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(fieldError ->
                        "El campo: ".concat(fieldError.getField())
                                .concat(" ")
                                .concat(fieldError.getDefaultMessage())
                    ).collect(Collectors.toList());
            responseError.setErrors(errors);
            if(errors.size() == 1){
                responseError.setError(errors.get(0));
                responseError.setErrors(null);
            }
        }
        String operation = requestMethod.name(); // Obtener el nombre del método HTTP (POST, PUT, DELETE, etc.)
        logger.info("Nombre del metodo HTTP: " + operation);
        responseError.setStatus("fail");
        switch (controllerName){
            case "UserRestController":
                switch (operation){
                    case "POST":
                        responseError.setMessage("Error en la creacion del usuario");
                        break;
                    case "PUT":
                        responseError.setMessage("Error en la actualización del usuario");
                        break;
                }
            case "RoleRestController":
                switch (operation){
                    case "POST":
                        responseError.setMessage("Error en la creacion del role");
                        break;
                    case "PUT":
                        responseError.setMessage("Error en la actualización del role");
                        break;
                }
        }
        return new ResponseEntity(responseError, HttpStatus.BAD_REQUEST);
    }

    //Para @Email
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ResponseError responseError = null;
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        if(errors.size() == 1){
             responseError = new ResponseError("fail", "Error en la validación", errors.get(0));
            return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
        }
        responseError = new ResponseError("fail", "Error en la validación", errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Error al convertir el valor: " + ex.getValue() + " a tipo " + ex.getRequiredType().getSimpleName();
        ResponseError responseError = new ResponseError("fail", "No se puede convertir el valor",errorMessage);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }

    /*
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String errorMessage = "La ruta solicitada '" + ex.getRequestURL() + "' no existe en el backend.";
        ResponseError responseError = new ResponseError("fail", "La ruta no existe",errorMessage);
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }*/

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleUserNotFoundException(UserNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail", "Error al buscar el usuario",errorMessage);
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PasswordUpdateNotAllowedException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handlePasswordUpdateNotAllowedException(PasswordUpdateNotAllowedException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error en la actualización del perfil del usuario",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    //pendiente
    /*@ExceptionHandler(UserCredentialsNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleUserCredentialsNotFoundException(UserCredentialsNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al iniciar session",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }
    //pendiente*/

    @ExceptionHandler(JwtValidationException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleJwtValidationException(JwtValidationException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","El token no es valido",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleSignatureException(io.jsonwebtoken.security.SignatureException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","El token no es valido",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseBody
    public ResponseEntity<ResponseError> handleExpiredJwtException(ExpiredJwtException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","El token ha expirado",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MalformedJwtException.class})
    @ResponseBody
    public ResponseEntity<ResponseError> handleMalformedJwtException(MalformedJwtException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","El token ha expirado",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(MappingException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleMappingException(MappingException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail",
                "Error al convertir los datos. Revise los tipos de datos y vuelva a intentarlo",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleRoleNotFoundException(RoleNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al buscar el role",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleDefaultHandlerExceptionResolver(HttpMessageNotReadableException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error no puede leer o interpretar el cuerpo  de la solicitud HTTP entrante.",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UpdateProfileNotAllowedException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleUpdateProfileNotAllowedException(UpdateProfileNotAllowedException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al actualizar el password",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleTokenNotFoundException(TokenNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al obtener el perfil",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailByTokenNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailByTokenNotFoundException(EmailByTokenNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al obtener el perfil",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailNotFoundException(EmailNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al enviar el correo",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailSendingException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailSendingException(EmailSendingException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al enviar el correo",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TokenNotExpiredException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleTokenNotExpiredException(TokenNotExpiredException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError("fail","Error al enviar el correo",errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.SERVICE_UNAVAILABLE);
    }

}
