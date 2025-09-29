package com.morago.backend.exception.phonenumber;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PhoneInvalidException extends ApiException {
    public PhoneInvalidException(String value) {
        super(HttpStatus.BAD_REQUEST, "PHONE_INVALID",
                "Phone must be 11 digits and start with 010 (e.g. 01012345678)");
    }
}