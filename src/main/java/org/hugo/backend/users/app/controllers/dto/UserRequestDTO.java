package org.hugo.backend.users.app.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.hugo.backend.users.app.models.entities.Role;

import java.util.List;


public class UserRequestDTO {
    @NotBlank(message = "no puede estar vacio", groups = {CreationValidation.class,UpdateValidation.class})
    private String name;
    @NotBlank(message = "no puede estar vacio", groups = {CreationValidation.class, UpdateValidation.class})
    private String lastname;
    @NotBlank(message = "no puede estar vacio", groups = {CreationValidation.class, UpdateValidation.class})
    @Email (message = "debe ser una dirección de correo electrónico válida",groups = {CreationValidation.class, UpdateValidation.class})
    private String email;
    @NotBlank(message = "no puede estar vacio",groups = {CreationValidation.class,UpdatePasswordValidation.class})
    private String password;
    @NotEmpty(message = "no puede estar vacio",groups = {CreationValidation.class, UpdateValidation.class})
    private List<Role> roles;

    public UserRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public interface CreationValidation{}

    public interface UpdateValidation{}
    public interface UpdatePasswordValidation{}


}
