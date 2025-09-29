package com.morago.backend.exception.file;

import com.morago.backend.exception.ResourceNotFoundException;

public class AvatarNotFoundException extends ResourceNotFoundException {
    public AvatarNotFoundException(Long userId) {
        super("Avatar not found for userId=" + userId);
    }
}