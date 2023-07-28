package org.hugo.backend.users.app.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hugo.backend.users.app.models.entities.User;
import org.hugo.backend.users.app.utils.TypeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }


    @Transactional
    public void sendEmail(String to, String subject, String from, TypeTemplate typeTemplate, User user) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(from);
        Context context = new Context();
        context.setVariable("user", user);
        String content = templateEngine.process(typeTemplate.getTypeTemplate(), context);
        helper.setText(content, true);
        javaMailSender.send(message);
    }
}

