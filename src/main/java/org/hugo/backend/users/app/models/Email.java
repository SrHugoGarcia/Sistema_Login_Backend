package org.hugo.backend.users.app.models;

import jakarta.validation.constraints.NotBlank;

public class Email {
    @NotBlank(message = "no puede estar vacio")
    private String to;
    @NotBlank(message = "no puede estar vacio")
    private String subject;
    @NotBlank(message = "no puede estar vacio")
    private String from;
    @NotBlank(message = "no puede estar vacio")
    private String name;
    public Email() {
    }

    public Email(String to, String subject, String from, String name) {
        this.to = to;
        this.subject = subject;
        this.from = from;
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
