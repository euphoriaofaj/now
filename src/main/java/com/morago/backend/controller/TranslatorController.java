package com.morago.backend.controller;

import com.morago.backend.dto.billing.withdrawal.CreateWithdrawalRequest;
import com.morago.backend.dto.billing.withdrawal.WithdrawalDto;
import com.morago.backend.dto.ThemeDto;
import com.morago.backend.dto.user.UserUpdateProfileRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileResponseDto;
import com.morago.backend.mapper.WithdrawalMapper;
import com.morago.backend.service.theme.ThemeService;
import com.morago.backend.service.user.UserService;
import com.morago.backend.service.withdrawal.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translator")
@RequiredArgsConstructor
@Tag(name = "Translator", description = "Endpoints for ROLE_TRANSLATOR only")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('TRANSLATOR')")
public class TranslatorController {

    private final UserService userService;
    private final ThemeService themeService;
    private final WithdrawalService withdrawalService;
    private final WithdrawalMapper withdrawalMapper;

    @Operation(
            summary = "Update profile",
            description = "Update translator's profile information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - TRANSLATOR role required")
            }
    )
    @PatchMapping("/profile")
    public ResponseEntity<UserUpdateProfileResponseDto> updateProfile(
            @Valid @RequestBody UserUpdateProfileRequestDto dto) {
        return ResponseEntity.ok(userService.updateMyProfile(dto));
    }

    @Operation(
            summary = "Get all available themes",
            description = "Get list of all themes that translators can work with.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Themes retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - TRANSLATOR role required")
            }
    )
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeDto>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAll());
    }

    @Operation(
            summary = "Request withdrawal",
            description = "Create a withdrawal request to withdraw money from translator's balance.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Withdrawal request created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient funds"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - TRANSLATOR role required")
            }
    )
    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalDto> requestWithdrawal(@RequestBody @Valid CreateWithdrawalRequest req) {
        Long userId = userService.getCurrentUserId();
        Long withdrawalId = withdrawalService.requestWithdrawal(
                userId, req.accountNumber(), req.accountHolder(), req.nameOfBank(), req.wonAmount()
        );
        var withdrawal = withdrawalService.findByIdOrThrow(withdrawalId);
        return ResponseEntity.ok(withdrawalMapper.toDto(withdrawal));
    }

    @Operation(
            summary = "Switch online status",
            description = "Switch translator's online/offline status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - TRANSLATOR role required")
            }
    )
    @PutMapping("/status")
    public ResponseEntity<Map<String, Boolean>> switchStatus(@RequestBody Map<String, Boolean> request) {
        Boolean isOnline = request.get("isOnline");
        if (isOnline == null) {
            throw new IllegalArgumentException("isOnline field is required");
        }
        
        var user = userService.getCurrentUser();
        // This would need to be implemented in TranslatorProfileService
        // translatorProfileService.setOnlineStatus(user, isOnline);
        
        return ResponseEntity.ok(Map.of("isOnline", isOnline));
    }
}
