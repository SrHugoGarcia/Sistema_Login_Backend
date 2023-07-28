package org.hugo.backend.users.app.controllers.dto;
import jakarta.validation.constraints.NotBlank;

public class UserAccountRequestDTO {
    @NotBlank(message = "no puede estar vacio",groups = {CreationValidation.class,UpdateProfileValidation.class})
    private String name;
    @NotBlank(message = "no puede estar vacio",groups = {CreationValidation.class,UpdateProfileValidation.class})
    private String lastname;
    @NotBlank(message = "no puede estar vacio",groups = {CreationValidation.class,
            UpdateProfileValidation.class,
            ForgetPasswordValidation.class})
    private String email;
    @NotBlank(message = "no puede estar vacio",groups = {CreationValidation.class,UpdatePasswordValidation.class})
    private String password;


    public UserAccountRequestDTO() {
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
    public interface CreationValidation{}

    public interface UpdateProfileValidation{}
    public interface UpdatePasswordValidation{}
    public interface ForgetPasswordValidation{}


}
