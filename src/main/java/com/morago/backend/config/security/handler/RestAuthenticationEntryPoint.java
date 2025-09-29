package com.morago.backend.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final SecurityErrorWriter writer;

    public RestAuthenticationEntryPoint(SecurityErrorWriter writer) {
        this.writer = writer;
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        String msg = (ex.getMessage() != null) ? ex.getMessage() : "Authentication required";
        writer.write(res, HttpStatus.UNAUTHORIZED, msg, req.getRequestURI(), null);
    }
}
