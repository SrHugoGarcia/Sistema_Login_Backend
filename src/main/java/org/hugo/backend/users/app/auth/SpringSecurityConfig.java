package org.hugo.backend.users.app.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpringSecurityConfig {
    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
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
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager());
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/forget-password").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/*/forget-password").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/logout").hasAnyRole(ROLE_USER,ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/v1/users/profile").hasAnyRole(ROLE_USER,ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/profile").hasAnyRole(ROLE_USER,ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/v1/users/password").hasAnyRole(ROLE_USER,ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST,"/api/v1/users").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/v1/users").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/v1/users/*").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/v1/users/*").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/users/*").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/v1/users/*/password").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST,"/api/v1/roles").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/v1/roles").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/v1/roles/*").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/v1/roles/*").hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/roles/*").hasAnyRole(ROLE_ADMIN)
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config->config.disable())
                .sessionManagement(managment->
                        managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
