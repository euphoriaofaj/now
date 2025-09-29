package com.morago.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User '%s' not found".formatted(username));
    }

    public UserNotFoundException(Long id) {
        super("User with id=%d not found".formatted(id));
    }
}
