package com.morago.backend.exception.rating;

import com.morago.backend.exception.ApiException;
import org.springframework.http.HttpStatus;

public class SelfRatingNotAllowedException extends ApiException {
    public SelfRatingNotAllowedException() {
        super(HttpStatus.FORBIDDEN, "You cannot rate yourself", "RATING_SELF_NOT_ALLOWED");
    }
}
