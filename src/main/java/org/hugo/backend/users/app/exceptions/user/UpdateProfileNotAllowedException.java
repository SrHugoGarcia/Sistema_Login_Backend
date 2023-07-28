package org.hugo.backend.users.app.exceptions.user;

public class UpdateProfileNotAllowedException extends RuntimeException{
    public UpdateProfileNotAllowedException(String message) {
        super(message);
    }
}
