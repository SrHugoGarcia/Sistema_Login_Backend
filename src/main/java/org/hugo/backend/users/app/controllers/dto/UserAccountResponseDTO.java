package org.hugo.backend.users.app.controllers.dto;
import org.hugo.backend.users.app.models.entities.Role;
import java.util.Date;
import java.util.List;

public class UserAccountResponseDTO {
    private String name;
    private String lastname;
    private String email;
    private Date createAt;

    public UserAccountResponseDTO() {
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
}
