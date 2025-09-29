package com.morago.backend.exception.rating;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RatingRequiresSuccessfulCallException extends ApiException {
    public RatingRequiresSuccessfulCallException() {
        super(HttpStatus.BAD_REQUEST, "You can rate only after a successful call", "RATING_REQUIRES_SUCCESSFUL_CALL");
    }
}