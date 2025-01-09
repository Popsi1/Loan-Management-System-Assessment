package com.example.usermodule.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.error("LoanUser:::{}:::: attempted to access URL :::::{}::::", authentication.getName(),
                    request.getRequestURI());
        }

        // Set the response status and content type
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write the error message as JSON
        String jsonResponse = String.format(
                "{\"error\": \"Access Denied\", \"message\": \"%s\", \"path\": \"%s\"}",
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );
        response.getWriter().write(jsonResponse);
    }

}
