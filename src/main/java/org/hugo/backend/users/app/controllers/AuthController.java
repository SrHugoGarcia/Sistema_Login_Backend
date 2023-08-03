package org.hugo.backend.users.app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.hugo.backend.users.app.controllers.dto.ApiResponse;
import org.hugo.backend.users.app.controllers.dto.UserAccountRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserAccountResponseDTO;
import org.hugo.backend.users.app.services.UserAccountService;
import org.hugo.backend.users.app.utils.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping(value = "${api.base-path}/auth")
@Validated
@CrossOrigin(originPatterns = "*")
public class AuthController {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * Registra un usuario.
     * @param userAccountRequestDTO Datos del usuario para registrar.
     * @param userAccountRequestDTO debe de incluir (name,lastname,email,password)
     * @return Respuesta con el usuario registrado.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserAccountRequestDTO userAccountRequestDTO){
        UserAccountResponseDTO userAccountResponseDTO = userAccountService.registerUser(userAccountRequestDTO);
        Map<String,UserAccountResponseDTO> userAccountResponseDTOMap = new HashMap<>();
        userAccountResponseDTOMap.put("user",userAccountResponseDTO);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL,"Usuario registrado correctamente",userAccountResponseDTOMap);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Permite cerrar session del usuario.
     * @param request se utiliza para extraer el token de los headers o de la cookie.
     * @param response se elimina el token de los headers y en la cookie se asigna un valor inválido.
     * @return status 204.
     */
    @GetMapping(value = ("/logout"))
    public void logout(HttpServletRequest request, HttpServletResponse response){
        userAccountService.logoutUser(request,response);
        response.setStatus(204);
        response.setContentType("application/json");
    }

    /**
     * A través de un token unico permite actualizar el password del usuario.
     * @param userAccountRequestDTO Dato del password.
     * @param userAccountRequestDTO debe de incluir (password)
     * @return Respuesta del password actualizado con exito.
     */
    @PostMapping(value = "/{token}/forget-password")
    public ResponseEntity<ApiResponse> restorePassword(@Validated(UserAccountRequestDTO.UpdatePasswordValidation.class)
                                             @RequestBody UserAccountRequestDTO userAccountRequestDTO,@PathVariable String token){
        userAccountService.restorePassword(token,userAccountRequestDTO);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL, "Password actualizado con exito",null);
        return new ResponseEntity(apiResponse,HttpStatus.OK);
    }

    /**
     * Enviá un correo al usuario con un token para actualizar su password.
     * @param userAccountRequestDTO Dato del correo.
     * @param userAccountRequestDTO debe de incluir (email)
     * @return Respuesta del correo enviado con exito.
     */
    @PostMapping(value = "/forget-password")
    public ResponseEntity<ApiResponse> forgetPassword(@Validated(UserAccountRequestDTO.ForgetPasswordValidation.class)
                                            @RequestBody UserAccountRequestDTO userAccountRequestDTO){
        userAccountService.forgotPassword(userAccountRequestDTO);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL,"Correo enviado con exito",null);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
}
