package org.hugo.backend.users.app.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Properties;

@Configuration
public class ObjectMappingConfig {
    // Bean definition for ModelMapper
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

