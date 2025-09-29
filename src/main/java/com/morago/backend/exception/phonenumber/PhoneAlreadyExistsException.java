package com.morago.backend.exception.phonenumber;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PhoneAlreadyExistsException extends ApiException {
    public PhoneAlreadyExistsException(String phone) {
        super(HttpStatus.CONFLICT, "PHONE_TAKEN", "Phone is already registered");
    }
}