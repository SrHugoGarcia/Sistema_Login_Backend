package org.hugo.backend.users.app.exceptions.user;

public class OrderInvalidException extends IllegalArgumentException{
    public OrderInvalidException(String s) {
        super(s);
    }
}
