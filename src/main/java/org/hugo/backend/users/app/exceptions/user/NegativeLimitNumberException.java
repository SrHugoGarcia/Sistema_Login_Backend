package org.hugo.backend.users.app.exceptions.user;

public class NegativeLimitNumberException extends IllegalArgumentException{
    public NegativeLimitNumberException(String s) {
        super(s);
    }
}
