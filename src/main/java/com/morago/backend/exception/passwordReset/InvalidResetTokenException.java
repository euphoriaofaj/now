package com.morago.backend.exception.passwordReset;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidResetTokenException extends ApiException {
    public InvalidResetTokenException() {
        super(HttpStatus.BAD_REQUEST, "Invalid or expired token", "AUTH_INVALID_RESET_TOKEN");
    }
}