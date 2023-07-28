package org.hugo.backend.users.app.exceptions.user;

public class PasswordUpdateNotAllowedException extends RuntimeException{

    public PasswordUpdateNotAllowedException(String message) {
        super(message);
    }
}
