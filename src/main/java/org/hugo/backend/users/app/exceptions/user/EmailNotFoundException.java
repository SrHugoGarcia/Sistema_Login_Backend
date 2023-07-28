package org.hugo.backend.users.app.exceptions.user;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String message) {
        super(message);
    }
}
