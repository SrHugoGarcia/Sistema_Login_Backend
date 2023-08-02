package org.hugo.backend.users.app.exceptions.user;

public class NegativePageNumberException extends IllegalArgumentException{
    public NegativePageNumberException(String s) {
        super(s);
    }
}
