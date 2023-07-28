package org.hugo.backend.users.app.exceptions.user;

public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String message) {
        super(message);
    }
}
