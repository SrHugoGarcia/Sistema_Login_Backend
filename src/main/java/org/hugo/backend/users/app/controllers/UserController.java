package org.hugo.backend.users.app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.hugo.backend.users.app.controllers.dto.*;
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
@RequestMapping(value = "${api.base-path}/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;
    private  final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Guarda un nuevo usuario en la base de datos.
     * Deben de incluir los roles en forma de arreglo.
     * @param userRequestDTO Datos del usuario a crear.
     * @param userRequestDTO debe de incluir (name,lastname,email,password,roles([{id,role}]))
     * @return Respuesta con el usuario recién creado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Validated({UserRequestDTO.CreationValidation.class}) @RequestBody UserRequestDTO userRequestDTO){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponse = userService.createOne(userRequestDTO);
        userMap.put("user", userResponse);
        ApiResponse apiResponse = new ApiResponse("successful","Usuario registrado con exito",userMap);
        return new ResponseEntity(apiResponse,HttpStatus.CREATED);
    }

    /**
     * Obtiene un usuario y actualiza su informacion.
     * Se puede actualizar todo excepto el password.
     * @param id del usuario a buscar.
     * @param userRequestDTO información del usuario para actualizar.
     * @param userRequestDTO debe de incluir (name,lastname,email,roles([{id,role}]))
     * @return Respuesta con el usuario actualizado.
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> update(@Validated({UserRequestDTO.UpdateValidation.class}) @RequestBody UserRequestDTO userRequestDTO, @PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponse = userService.updateOne(userRequestDTO,id);
        userMap.put("user", userResponse);
        ApiResponse apiResponse = new ApiResponse("successful","Usuario actualizado con exito",userMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    /**
     * Obtiene y elimina al usuario.
     * @param id del usuario a buscar.
     * @return NO_CONTENT.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String,Object> response = new HashMap<>();
        userService.deleteOne(id);
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }

    /**
     * Obtiene un usuario en la base de datos.
     * Incluye el identificador del usuario y sus roles en la respuesta.
     * @param id del usuario a buscar.
     * @return Respuesta con el usuario obtenido.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponseDTO = userService.findById(id);
        userMap.put("user", userResponseDTO);
        ApiResponse apiResponse = new ApiResponse("successful","Usuario obtenido con exito",userMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     * Incluye el id de los usuarios y los roles.
     * @return Respuesta con el usuario recién creado.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(){
        Map<String, List<UserResponseDTO>> listMap = new HashMap<>();
        List<UserResponseDTO> users = userService.findAll();
        listMap.put("users", users);
        ApiResponse apiResponse = new ApiResponse("successful","Usuarios obtenidos con exito",listMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    /**
     * Obtiene un usuario y actualiza su password.
     * Se puede actualizar solo el password.
     * @param id del usuario a buscar.
     * @param userRequestDTO password del usuario paar actualizar.
     * @param userRequestDTO debe de incluir (password)
     * @return Respuesta con el usuario actualizado.
     */
    @PutMapping(value = "/{id}/password")
    public ResponseEntity<ApiResponse> updatePasswordById(@Validated({UserRequestDTO.UpdatePasswordValidation.class})
                                            @RequestBody UserRequestDTO userRequestDTO,
                                            @PathVariable Long id){
        Map<String,UserResponseDTO> userMap = new HashMap<>();
        UserResponseDTO userResponseDTO  = userService.updatePassword(userRequestDTO,id);
        userMap.put("user", userResponseDTO);
        ApiResponse apiResponse = new ApiResponse("successful","Password actualizado con exito",userMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    /**
     * Actualiza el perfil de un usuario.
     * @param request se utiliza para extraer el token de los headers o de la cookie.
     * @param userAccountRequestDTO Datos del usuario para actualizar.
     * @param userAccountRequestDTO No se permite actualizar el password y los roles.
     * @param userAccountRequestDTO debe de incluir (name,lastname,email)
     * @return Respuesta con el perfil actualizado.
     */
    @PutMapping(value = "/profile")
    public ResponseEntity<ApiResponse> updateProfile(@Validated({UserAccountRequestDTO.UpdateProfileValidation.class})
                                               @RequestBody UserAccountRequestDTO userAccountRequestDTO,
                                               HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userMap = new HashMap<>();
        UserAccountResponseDTO userAccountResponseDTO  = userAccountService.updateProfile(request,userAccountRequestDTO);
        userMap.put("user", userAccountResponseDTO);
        ApiResponse apiResponse = new ApiResponse( "successful","Perfil actualizado con exito",userMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    /**
     * Actualiza el password del usuario.
     * @param request se utiliza para extraer el token de los headers o de la cookie.
     * @param userAccountRequestDTO Dato del password.
     * @param userAccountRequestDTO debe de incluir (password)
     * @return Respuesta del password actualizado con exito.
     * @return Respuesta con el perfil actualizado.
     */
    @PutMapping(value = "/password")
    public ResponseEntity<ApiResponse> updatePassword(@Validated({UserAccountRequestDTO.UpdatePasswordValidation.class})
                                           @RequestBody UserAccountRequestDTO userAccountRequestDTO,
                                            HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userMap = new HashMap<>();
        UserAccountResponseDTO userAccountResponseDTO  = userAccountService.updatePassword(request,userAccountRequestDTO);
        userMap.put("user", userAccountResponseDTO);
        ApiResponse apiResponse = new ApiResponse( "successful","Password actualizado con exito",userMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    /**
     * Obtiene el perfil del usuario.
     * @param request se utiliza para extraer el token de los headers o de la cookie.
     * @return Respuesta con el perfil obtenido.
     */
    @GetMapping(value = "/profile")
    public ResponseEntity<ApiResponse> profile(HttpServletRequest request){
        Map<String,UserAccountResponseDTO> userAccountResponseDTOMap = new HashMap<>();
        UserAccountResponseDTO userResponseDTO = userAccountService.getProfile(request);
        userAccountResponseDTOMap.put("user", userResponseDTO);
        ApiResponse apiResponse = new ApiResponse("successful","Perfil obtenido con exito",userAccountResponseDTOMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

}
