package org.hugo.backend.users.app.auth;

import org.hugo.backend.users.app.auth.filters.JwtAuthenticationFilter;
import org.hugo.backend.users.app.auth.filters.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager() throws  Exception{
        return  authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/users/**").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/logout").hasAnyRole("USER")
                        .requestMatchers("/api/v1/roles").hasAnyRole("USER")
                        .requestMatchers("/api/v1/roles/**").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/*/profile").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/profile").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/*/password").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/password").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/forget-password").hasAnyRole("USER")
                        .requestMatchers("/api/v1/users/*/forget-password").hasAnyRole("USER")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config->config.disable())
                .sessionManagement(managment->
                        managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
