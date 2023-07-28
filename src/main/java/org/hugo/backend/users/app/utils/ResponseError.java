package org.hugo.backend.users.app.utils;

import java.util.List;

public class ResponseError {
    private String status;
    private String message;
    private String error;
    private List<String> errors;

    public ResponseError() {
    }

    public ResponseError(String status, String message, String error){
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public ResponseError(String status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
