package com.morago.backend.dto.passwordReset;

public record PasswordResetVerifyDto(String phone, Integer code) {}