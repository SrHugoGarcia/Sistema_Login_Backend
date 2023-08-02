package org.hugo.backend.users.app.utils;

public enum TemplateType {
    FORGET_PASSWORD_TEMPLATE("email_forget_password");
    private String typeTemplate;
    TemplateType(String template){
        this.typeTemplate = template;
    }

    public String getTypeTemplate() {
        return typeTemplate;
    }
}
