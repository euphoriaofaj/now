package com.morago.backend.controller;

import com.morago.backend.dto.billing.deposit.CreateDepositRequest;
import com.morago.backend.dto.billing.deposit.DepositDto;
import com.morago.backend.dto.translator.TranslatorProfileDto;
import com.morago.backend.dto.user.UserUpdateProfileRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileResponseDto;
import com.morago.backend.mapper.DepositMapper;
import com.morago.backend.service.deposit.DepositService;
import com.morago.backend.service.profile.TranslatorProfileService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for ROLE_USER only")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final TranslatorProfileService translatorProfileService;
    private final DepositService depositService;
    private final DepositMapper depositMapper;
    private final UserService userService;

    @Operation(
            summary = "Get translators by theme",
            description = "Get list of translators who handle a specific theme.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Translators retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - USER role required")
            }
    )
    @GetMapping("/translators/by-theme/{themeId}")
    public ResponseEntity<List<TranslatorProfileDto>> getTranslatorsByTheme(@PathVariable Long themeId) {
        return ResponseEntity.ok(translatorProfileService.getTranslatorsByTheme(themeId));
    }

    @Operation(
            summary = "Get translator info by ID",
            description = "Get detailed information about a specific translator.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Translator info retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - USER role required"),
                    @ApiResponse(responseCode = "404", description = "Translator not found")
            }
    )
    @GetMapping("/translators/{translatorId}")
    public ResponseEntity<TranslatorProfileDto> getTranslatorById(@PathVariable Long translatorId) {
        return ResponseEntity.ok(translatorProfileService.getById(translatorId));
    }

    @Operation(
            summary = "Deposit money",
            description = "Create a deposit request to add money to user's balance.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deposit request created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - USER role required")
            }
    )
    @PostMapping("/deposits")
    public ResponseEntity<DepositDto> createDeposit(@RequestBody @Valid CreateDepositRequest req) {
        Long userId = userService.getCurrentUserId();
        Long depositId = depositService.createDeposit(userId, req.accountHolder(), req.nameOfBank(), req.wonAmount());
        var deposit = depositService.findByIdOrThrow(depositId);
        return ResponseEntity.ok(depositMapper.toDto(deposit));
    }

    @Operation(
            summary = "Update profile",
            description = "Update user's profile information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - USER role required")
            }
    )
    @PatchMapping("/profile")
    public ResponseEntity<UserUpdateProfileResponseDto> updateProfile(
            @Valid @RequestBody UserUpdateProfileRequestDto dto) {
        return ResponseEntity.ok(userService.updateMyProfile(dto));
    }

    @Operation(
            summary = "Delete profile",
            description = "Delete user's profile and account.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - USER role required")
            }
    )
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile() {
        userService.deleteMyProfile();
    }
}
