package org.hugo.backend.users.app.controllers.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.utils.PaginatedResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Date createAt;
    private List<Role> roles;

    public UserResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserResponseDTO filterFields(List<String> fields) {
        UserResponseDTO filteredDTO = new UserResponseDTO();

        for (String field : fields) {
            switch (field) {
                case "id":
                    if (this.id != null) {
                        filteredDTO.setId(this.id);
                    }
                    break;
                case "name":
                    if (this.name != null) {
                        filteredDTO.setName(this.name);
                    }
                    break;
                case "lastname":
                    if (this.lastname != null) {
                        filteredDTO.setLastname(this.lastname);
                    }
                    break;
                case "email":
                    if (this.email != null) {
                        filteredDTO.setEmail(this.email);
                    }
                    break;
                case "roles":
                    if (this.roles != null) {
                        filteredDTO.setRoles(this.roles);
                    }
                    break;
                case "createAt":
                    if (this.createAt != null) {
                        filteredDTO.setCreateAt(this.createAt);
                    }
                    break;
                default:
                    // Ignorar campos no permitidos en la consulta.
                    break;
            }
        }

        return filteredDTO;
    }

    public void filterFieldsAndRemove(List<String> fields) {
        List<String> fieldsToRemove = new ArrayList<>();
        for (String field : fields) {
            if (!isValidField(field)) {
                fieldsToRemove.add(field);
            }
        }
        fields.removeAll(fieldsToRemove);
    }


    private boolean isValidField(String field) {
        switch (field) {
            case "id":
            case "name":
            case "lastname":
            case "email":
            case "roles":
            case "createAt":
                return true;
            default:
                return false;
        }
    }
}

