package org.hugo.backend.users.app.controllers.dto;
/**
 * Construye la respuesta de uso general.
 * Status informar al cliente del resultado de la peticion(successful o fail).
 * Message informa al cliente con un mensaje.
 * Sata se utiliza cuando es necesario enviar información.
 */
public class ApiResponse {
    private String status;
    private String message;
    private Object data;

    public ApiResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
