package com.morago.backend.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @Schema(example = "oldPassword123!", description = "Current password. Admins may omit it.")
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    @Schema(example = "NewPassword123!", description = "New password")
    private String newPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    @Schema(example = "NewPassword123!", description = "Confirmation of new password")
    private String confirmPassword;
}