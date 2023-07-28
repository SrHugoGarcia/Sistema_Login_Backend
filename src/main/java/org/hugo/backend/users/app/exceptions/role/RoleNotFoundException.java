package org.hugo.backend.users.app.exceptions.role;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message) {
        super(message);
    }
}
