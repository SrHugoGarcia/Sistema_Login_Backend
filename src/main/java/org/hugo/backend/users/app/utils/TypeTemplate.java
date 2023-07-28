package org.hugo.backend.users.app.utils;

public enum TypeTemplate {
    FORGET_PASSWORD_TEMPLATE("email_forget_password");
    private String typeTemplate;
    TypeTemplate(String template){
        this.typeTemplate = template;
    }

    public String getTypeTemplate() {
        return typeTemplate;
    }
}
