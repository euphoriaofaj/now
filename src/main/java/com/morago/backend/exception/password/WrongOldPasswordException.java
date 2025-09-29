package com.morago.backend.exception.password;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class WrongOldPasswordException extends ApiException {
    public WrongOldPasswordException() {
        super(HttpStatus.BAD_REQUEST, "Old password is incorrect", "WRONG_OLD_PASSWORD");
    }
}