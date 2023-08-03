package org.hugo.backend.users.app.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.auth.filters.JwtAuthenticationFilter;
import org.hugo.backend.users.app.auth.filters.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
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
                        .requestMatchers(HttpMethod.GET,"/api/v1/users").hasAnyRole(ROLE_ADMIN,ROLE_USER)
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
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config =new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("POST","PUT","DELETE","GET"));
        config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);//se aplica en todas las rutas
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter>corsFilter(){
        FilterRegistrationBean<CorsFilter> bean =new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);//se registra el bean y se le da una alta prioridad
        return bean;
    }
}
