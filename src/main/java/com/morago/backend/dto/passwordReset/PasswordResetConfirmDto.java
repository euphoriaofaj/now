package com.morago.backend.dto.passwordReset;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmDto(
        @NotBlank String token,
        @NotBlank String newPassword
) {}