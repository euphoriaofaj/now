package com.morago.backend.exception.token;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super("Invalid Jwt Token");
    }
}
