package org.hugo.backend.users.app.exceptions.user;

public class TokenNotExpiredException extends RuntimeException{
    public TokenNotExpiredException(String message) {
        super(message);
    }
}
