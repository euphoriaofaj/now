package com.morago.backend.controller;


import com.morago.backend.dto.passwordReset.PasswordResetConfirmDto;
import com.morago.backend.dto.passwordReset.PasswordResetRequestDto;
import com.morago.backend.dto.passwordReset.PasswordResetVerifyDto;
import com.morago.backend.service.passwordReset.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
@Tag(name = "Auth: Password reset")
public class AuthPasswordController {
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Request password reset code")
    @ApiResponse(responseCode = "204", description = "Code sent")
    @PostMapping("/reset/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void request(@RequestBody PasswordResetRequestDto dto) {
        passwordResetService.startReset(dto.phone());
    }

    @Operation(summary = "Verify code and get token")
    @PostMapping("/reset/verify")
    public Map<String,String> verify(@RequestBody PasswordResetVerifyDto dto) {
        String token = passwordResetService.verifyCode(dto.phone(), dto.code());
        return Map.of("token", token);
    }

    @Operation(summary = "Confirm reset with token and set new password")
    @ApiResponse(responseCode = "204", description = "Password changed")
    @PostMapping("/reset/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@Valid @RequestBody PasswordResetConfirmDto dto) {
        passwordResetService.confirm(dto.token(), dto.newPassword());
    }
}
