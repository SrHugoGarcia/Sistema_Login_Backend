package org.hugo.backend.users.app.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hugo.backend.users.app.utils.ResponseError;
import org.hugo.backend.users.app.utils.StatusType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.hugo.backend.users.app.global.ErrorMessages.ROUTE_ACCESS_DENIED_ERROR_MSG;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Puedes establecer cualquier otro estado que desees
        response.setContentType("application/json");
        ResponseError responseError = new ResponseError(StatusType.FAIL,ROUTE_ACCESS_DENIED_ERROR_MSG,accessDeniedException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), responseError);
    }


}

