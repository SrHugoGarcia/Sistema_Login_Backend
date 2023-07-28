package org.hugo.backend.users.app.exceptions.user;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException(String message) {
        super(message);
    }
}
