package org.hugo.backend.users.app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.hugo.backend.users.app.controllers.dto.UserAccountRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserAccountResponseDTO;
import org.hugo.backend.users.app.controllers.dto.UserRequestDTO;
import org.hugo.backend.users.app.controllers.dto.UserResponseDTO;
import org.hugo.backend.users.app.services.UserAccountService;
import org.hugo.backend.users.app.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/users")
@Validated
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;
    private  final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param userRequestDTO Datos del usuario a crear.
     * @return Respuesta con el usuario recién creado.
     */
    @PostMapping
    public ResponseEntity<?> save(@Validated({UserRequestDTO.CreationValidation.class}) @RequestBody UserRequestDTO userRequestDTO){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponse = userService.createOne(userRequestDTO);
        userMap.put("user", userResponse);
        return buildResponse("successful","Usuario registrado con exito",userMap,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> findAll(){
        Map<String, List<UserResponseDTO>> listMap = new HashMap<>();
        List<UserResponseDTO> users = userService.findAll();
        listMap.put("users", users);
        return buildResponse("successful","Usuarios obtenidos con exito",listMap,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponseDTO = userService.findById(id);
        userMap.put("user", userResponseDTO);
        return buildResponse("successful","Usuario obtenido con exito",userMap,HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@Validated({UserRequestDTO.UpdateValidation.class}) @RequestBody UserRequestDTO userRequestDTO, @PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponse = userService.updateOne(userRequestDTO,id);
        userMap.put("user", userResponse);
        return buildResponse("successful","Usuario actualizado con exito",userMap,HttpStatus.OK);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String,Object> response = new HashMap<>();
        userService.deleteOne(id);
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}/password")
    public ResponseEntity<?> updatePasswordById(@Validated({UserRequestDTO.UpdatePasswordValidation.class})
                                            @RequestBody UserRequestDTO userRequestDTO,
                                            @PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponseDTO  = userService.updatePassword(userRequestDTO,id);
        userMap.put("user", userResponseDTO);
        return buildResponse("successful","Password actualizado con exito",userMap,HttpStatus.OK);
    }
    @PostMapping(value = "/forget-password")
    public ResponseEntity<?> forgetPassword(@Validated(UserAccountRequestDTO.ForgetPasswordValidation.class)
                                                @RequestBody UserAccountRequestDTO userAccountRequestDTO){
        userAccountService.forgotPassword(userAccountRequestDTO);
        return buildResponse("successful","Correo enviado con exito",null,HttpStatus.OK);
    }

    @PostMapping(value = "/{token}/forget-password")
    public ResponseEntity<?> restorePassword(@Validated(UserAccountRequestDTO.UpdatePasswordValidation.class)
                                             @RequestBody UserAccountRequestDTO userAccountRequestDTO,@PathVariable String token){
        userAccountService.restorePassword(token,userAccountRequestDTO);
        return buildResponse("successful","Password actualizado con exito",null,HttpStatus.OK);
    }
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserAccountRequestDTO userAccountRequestDTO){
        UserAccountResponseDTO userAccountResponseDTO = userAccountService.registerUser(userAccountRequestDTO);
        Map<String,UserAccountResponseDTO> userAccountResponseDTOMap = new HashMap<>();
        userAccountResponseDTOMap.put("user",userAccountResponseDTO);
        return buildResponse("successful","Usuario registrado correctamente",
                userAccountResponseDTOMap,HttpStatus.CREATED);
    }
    @PutMapping(value = "/profile")
    public ResponseEntity<?> updateProfile(@Validated({UserAccountRequestDTO.UpdateProfileValidation.class})
                                               @RequestBody UserAccountRequestDTO userAccountRequestDTO,
                                               HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userMap = new HashMap<>();
        UserAccountResponseDTO userAccountResponseDTO  = userAccountService.updateProfile(request,userAccountRequestDTO);
        userMap.put("user", userAccountResponseDTO);
        return buildResponse("successful","Perfil actualizado con exito",userMap,HttpStatus.OK);
    }

    @PutMapping(value = "/password")
    public ResponseEntity<?> updatePassword(@Validated({UserAccountRequestDTO.UpdatePasswordValidation.class})
                                           @RequestBody UserAccountRequestDTO userAccountRequestDTO,
                                            HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userMap = new HashMap<>();
        UserAccountResponseDTO userAccountResponseDTO  = userAccountService.updatePassword(request,userAccountRequestDTO);
        userMap.put("user", userAccountResponseDTO);
        return buildResponse("successful","Password actualizado con exito",userMap,HttpStatus.OK);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<?> profile(HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userAccountResponseDTOMap = new HashMap<>();
        UserAccountResponseDTO userResponseDTO = userAccountService.getProfile(request);
        userAccountResponseDTOMap.put("user", userResponseDTO);
        return buildResponse("successful","Perfil obtenido con exito",userAccountResponseDTOMap,HttpStatus.OK);
    }

    @PostMapping(value = ("/logout"))
    public void logout(HttpServletRequest request, HttpServletResponse response){
        userAccountService.logoutUser(request,response);
        response.setStatus(204);
        response.setContentType("application/json");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String status, String message, Object data,HttpStatus httpStatus) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response,httpStatus);
    }

}
