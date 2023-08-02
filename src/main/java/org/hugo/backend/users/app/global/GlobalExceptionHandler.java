package org.hugo.backend.users.app.global;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.MappingException;
import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.exceptions.user.*;
import org.hugo.backend.users.app.utils.ResponseError;
import org.hugo.backend.users.app.utils.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hugo.backend.users.app.global.ErrorMessages.*;

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
        //logger.info("Nombre del controlador: " + controllerName);
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
       // logger.info("Nombre del metodo HTTP: " + operation);
        responseError.setStatus(StatusType.FAIL);
        switch (controllerName){
            case "UserRestController":
                switch (operation){
                    case "POST":
                        responseError.setMessage(USER_CREATION_ERROR_MSG);
                        break;
                    case "PUT":
                        responseError.setMessage(USER_UPDATE_ERROR_MSG);
                        break;
                }
            case "RoleRestController":
                switch (operation){
                    case "POST":
                        responseError.setMessage(ROLE_CREATION_ERROR_MSG);
                        break;
                    case "PUT":
                        responseError.setMessage(ROLE_UPDATE_ERROR_MSG);
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
             responseError = new ResponseError(StatusType.FAIL, VALIDATION_ERROR_MSG, errors.get(0));
            return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
        }
        responseError = new ResponseError(StatusType.FAIL, VALIDATION_ERROR_MSG, errors);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Error al convertir el valor: " + ex.getValue() + " a tipo " + ex.getRequiredType().getSimpleName();
        ResponseError responseError = new ResponseError(StatusType.FAIL, CONVERSION_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleUserNotFoundException(UserNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL, USER_NOT_FOUND_MSG,errorMessage);
        return new ResponseEntity<>(responseError, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PasswordUpdateNotAllowedException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handlePasswordUpdateNotAllowedException(PasswordUpdateNotAllowedException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,USER_PROFILE_UPDATE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(JwtValidationException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleJwtValidationException(JwtValidationException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,TOKEN_INVALID_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleSignatureException(io.jsonwebtoken.security.SignatureException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,TOKEN_INVALID_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseBody
    public ResponseEntity<ResponseError> handleExpiredJwtException(ExpiredJwtException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,EXPIRED_TOKEN_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MalformedJwtException.class})
    @ResponseBody
    public ResponseEntity<ResponseError> handleMalformedJwtException(MalformedJwtException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,EXPIRED_TOKEN_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(MappingException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleMappingException(MappingException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,
                DATA_CONVERSION_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleRoleNotFoundException(RoleNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,ROLE_NOT_FOUND_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleDefaultHandlerExceptionResolver(HttpMessageNotReadableException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,HTTP_BODY_READ_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UpdateProfileNotAllowedException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleUpdateProfileNotAllowedException(UpdateProfileNotAllowedException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,USER_PASSWORD_UPDATE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleTokenNotFoundException(TokenNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,USER_PERFIL_NOT_FOUND_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailByTokenNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailByTokenNotFoundException(EmailByTokenNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,USER_PERFIL_NOT_FOUND_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailNotFoundException(EmailNotFoundException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,EMAIL_SENDING_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailSendingException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleEmailSendingException(EmailSendingException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,EMAIL_SENDING_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TokenNotExpiredException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleTokenNotExpiredException(TokenNotExpiredException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,EMAIL_SENDING_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.SERVICE_UNAVAILABLE);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,INVALID_ARGUMENT,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NegativeLimitNumberException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleNegativeLimitNumberException(NegativeLimitNumberException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,INVALID_LIMIT_VALUE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NegativePageNumberException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleNegativePageNumberException(NegativePageNumberException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,INVALID_PAGE_VALUE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderInvalidException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handleOrderInvalidException(OrderInvalidException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,INVALID_ORDER_VALUE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseBody
    public ResponseEntity<ResponseError> handlePropertyReferenceException(PropertyReferenceException ex){
        String errorMessage = ex.getMessage();
        ResponseError responseError = new ResponseError(StatusType.FAIL,INVALID_SIZE_VALUE_ERROR_MSG,errorMessage);
        return new ResponseEntity<>(responseError,HttpStatus.BAD_REQUEST);
    }

}
