package org.hugo.backend.users.app.controllers;

import jakarta.validation.Valid;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/roles")
public class RoleRestController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Role role){
        Role roleSave = roleService.createOne(role);
        return buildResponse("successful", "Role creado con exito",
                roleSave,HttpStatus.CREATED);
    }

    @PutMapping(value ="/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Role role,@PathVariable Long id){
        Role roleUpdate = roleService.updateOne(role,id);
        return buildResponse("successful","Role actualizado con exito",roleUpdate,HttpStatus.OK);
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Role role = roleService.findById(id);
        return buildResponse("successful","Role obtenido con exito", role,HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<?> findAll(){
        List<Role> roles = roleService.findAll();
        Map<String,List<Role>> listMap = new HashMap<>();
        listMap.put("roles",roles);
        return buildResponse("successful", "Roles obtenidos con exito",listMap,HttpStatus.OK);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String,Object> response = new HashMap<>();
        roleService.deleteOne(id);
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<Map<String,Object>> buildResponse(String status, String message, Object data, HttpStatus httpStatus){
        Map<String,Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message",message);
        response.put("data",data);
        return new ResponseEntity<>(response,httpStatus);
    }
}
