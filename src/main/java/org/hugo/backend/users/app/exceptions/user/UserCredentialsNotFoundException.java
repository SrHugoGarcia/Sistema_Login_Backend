package org.hugo.backend.users.app.exceptions.user;

public class UserCredentialsNotFoundException extends RuntimeException{
    public UserCredentialsNotFoundException(String message) {
        super(message);
    }
}
