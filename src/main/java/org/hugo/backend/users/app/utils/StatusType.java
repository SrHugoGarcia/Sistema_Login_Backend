package org.hugo.backend.users.app.utils;

public enum StatusType {
    FAIL("fail"),
    SUCCESSFUL("successful");
    private String type;
    StatusType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
