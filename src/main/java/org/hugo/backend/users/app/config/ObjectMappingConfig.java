package org.hugo.backend.users.app.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMappingConfig {
    // Bean definition for ModelMapper
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

