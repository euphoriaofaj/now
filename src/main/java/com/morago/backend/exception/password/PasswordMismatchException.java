package com.morago.backend.exception.password;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends ApiException {
    public PasswordMismatchException() {
        super(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "Passwords do not match");
    }
}