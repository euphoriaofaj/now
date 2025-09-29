package com.morago.backend.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morago.backend.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Component
public class SecurityErrorWriter {
    private final ObjectMapper objectMapper;

    public SecurityErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse res, HttpStatus status, String message, String path, String code) throws IOException {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                code,
                path,
                Instant.now(),
                null
        );
        res.setStatus(status.value());
        res.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(res.getWriter(), body);
    }
}
