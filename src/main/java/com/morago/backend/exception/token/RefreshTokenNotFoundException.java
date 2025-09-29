package com.morago.backend.exception.token;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("Refresh Token not found or invalid");
    }
}
