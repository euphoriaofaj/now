package com.morago.backend.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityErrorWriter writer;

    public RestAccessDeniedHandler(SecurityErrorWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        String msg = (ex.getMessage() != null) ? ex.getMessage() : "Access is denied";
        writer.write(res, HttpStatus.FORBIDDEN, msg, req.getRequestURI(), null);
    }
}
