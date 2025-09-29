package com.morago.backend.exception.token;

public class ExpireJwtTokenException extends RuntimeException {
    public ExpireJwtTokenException() {
        super("Jwt Token has expired");
    }
}
