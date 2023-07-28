package org.hugo.backend.users.app.exceptions.user;

public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String message) {
        super(message);
    }
}
