package org.hugo.backend.users.app.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.auth.TokenJwtConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;
public class JwtValidationFilter extends BasicAuthenticationFilter {
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //procesa cada solicitud
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = TokenJwtConfig.getTokenFromRequest(request);
       if (token == null || token.isEmpty()) {
            sendUnauthorizedError(response);
            return;
        }
        try {
            Claims claims = TokenJwtConfig.parseToken(token);
            String email = claims.getSubject();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            sendJwtErrorResponse(response, e);
        }
    }


    private void sendJwtErrorResponse(HttpServletResponse response, JwtException e) throws IOException {
        Map<String, String> body = Map.of(
                "status", "fail",
                "message", "El token no es valido",
                "error", e.getMessage()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(403);
        response.setContentType("application/json");
    }

    private void sendUnauthorizedError(HttpServletResponse response) throws IOException {
        Map<String, String> body = Map.of(
                "status", "fail",
                "message", "No autorizado. Inicie sesión para acceder a este recurso",
                "error", "Unauthorized"
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
    }
}