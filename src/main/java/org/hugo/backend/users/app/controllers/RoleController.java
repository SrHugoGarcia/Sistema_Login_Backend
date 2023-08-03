package org.hugo.backend.users.app.controllers;

import jakarta.validation.Valid;
import org.hugo.backend.users.app.controllers.dto.ApiResponse;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.services.RoleService;
import org.hugo.backend.users.app.utils.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "${api.base-path}/roles")
@CrossOrigin(originPatterns = "*")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * Guarda un nuevo role en la base de datos.
     * @param role Dato del role a crear.
     * @param role debe de incluir (role)
     * @return Respuesta con el role recién creado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody Role role){
        Role roleSave = roleService.createOne(role);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL, "Role creado con exito",
                roleSave);
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    /**
     * Actualiza un nuevo role en la base de datos.
     * @param id del rol a buscar
     * @param role Dato del role a actualizar.
     * @param role debe de incluir (role)
     * @return Respuesta con él role recién actualizado.
     */
    @PutMapping(value ="/{id}")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody Role role,@PathVariable Long id){
        Role roleUpdate = roleService.updateOne(role,id);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL,"Role actualizado con exito",roleUpdate);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    /**
     * Obtiene un role en la base de datos.
     * @param id del rol a buscar
     * @return Respuesta con él role recién obtenido.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id){
        Role role = roleService.findById(id);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL,"Role obtenido con exito", role);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    /**
     * Obtiene todos los roles en la base de datos.
     * @return Respuesta con todos los roles obtenidos.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(){
        List<Role> roles = roleService.findAll();
        Map<String,List<Role>> listMap = new HashMap<>();
        listMap.put("roles",roles);
        ApiResponse apiResponse = new ApiResponse(StatusType.SUCCESSFUL, "Roles obtenidos con exito",listMap);
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    /**
     * Elimina un role en la base de datos.
     * @param id del rol a buscar
     * @return NO_CONTENT.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String,Object> response = new HashMap<>();
        roleService.deleteOne(id);
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }
}
