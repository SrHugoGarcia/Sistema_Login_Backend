package org.hugo.backend.users.app.utils;



public enum OrderType {
    ASCENDING("asc"),
    DESCENDING("desc");
    private String type;
    OrderType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // Método estático para obtener el Enum correspondiente a partir del valor String.
    public static OrderType fromValue(String value) {
        for (OrderType order : OrderType.values()) {
            if (order.getType().equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new IllegalArgumentException("Ordenamiento inválido: " + value);
    }
}
