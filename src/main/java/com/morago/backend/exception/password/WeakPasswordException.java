package com.morago.backend.exception.password;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class WeakPasswordException extends ApiException {
    public WeakPasswordException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "WEAK_PASSWORD");
    }
}
