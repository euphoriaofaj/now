package com.morago.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final Map<String, String> errors;

    protected ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, null);
    }
    protected ApiException(HttpStatus status, String code, String message, Map<String, String> errors) {
        super(message);
        this.status = status;
        this.code = code;
        this.errors = errors;
    }
}