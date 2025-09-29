package com.morago.backend.exception.passwordReset;


import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class MissingVerifyFieldsException extends ApiException {
  public MissingVerifyFieldsException() {
    super(HttpStatus.BAD_REQUEST, "Phone and code are required", "AUTH_RESET_VERIFY_MISSING_FIELDS");
  }
}