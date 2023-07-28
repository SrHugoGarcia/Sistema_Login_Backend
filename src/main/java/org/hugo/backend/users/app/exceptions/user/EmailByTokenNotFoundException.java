package org.hugo.backend.users.app.exceptions.user;

public class EmailByTokenNotFoundException extends RuntimeException{
    public EmailByTokenNotFoundException(String message) {
        super(message);
    }
}
