package org.hugo.backend.users.app.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.auth.TokenJwtConfig;
import org.hugo.backend.users.app.models.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            if (user.getEmail() == null || user.getPassword() == null) {
                //throw new UserCredentialsNotFoundException("Error en la autenticacion, email o password incorrecto");
            }
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String email = authResult.getName();

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TokenJwtConfig.TOKEN_EXPIRATION_TIME))
                .signWith(TokenJwtConfig.SECRET_KEY)
                .compact();

        response.addHeader(TokenJwtConfig.HEADER_AUTHORIZATION, TokenJwtConfig.PREFIX_TOKEN + token);
        response.addCookie(TokenJwtConfig.createTokenCookie(token));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "successful");
        responseBody.put("message", "Has iniciado sesion con exito");
        responseBody.put("token", token);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException{
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "fail");
        responseBody.put("message", "Error en la autenticacion, email o password incorrecto");
        responseBody.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
    }

}