package com.morago.backend.controller;

import com.morago.backend.dto.billing.transaction.MyTransactionDto;
import com.morago.backend.dto.call.CallDto;
import com.morago.backend.dto.FileResponse;
import com.morago.backend.dto.NotificationDto;
import com.morago.backend.dto.password.ChangePasswordRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileResponseDto;
import com.morago.backend.mapper.TransactionMapper;
import com.morago.backend.service.call.CallService;
import com.morago.backend.service.file.FileService;
import com.morago.backend.service.notification.NotificationService;
import com.morago.backend.service.transaction.TransactionService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Me", description = "Endpoints for USER and TRANSLATOR to manage their own data")
@SecurityRequirement(name = "bearerAuth")
public class MeController {

    private final UserService userService;
    private final FileService fileService;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final CallService callService;

    @Operation(
            summary = "Update profile",
            description = "Allows an authenticated user to update their profile information.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateProfileRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                            content = @Content(schema = @Schema(implementation = UserUpdateProfileResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PatchMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR','ADMIN')")
    public UserUpdateProfileResponseDto updateMyProfile(
            @Valid @RequestBody UserUpdateProfileRequestDto dto) {
        return userService.updateMyProfile(dto);
    }

    @Operation(
            summary = "Change password",
            description = "Allows an authenticated user to change their password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password successfully changed"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PatchMapping("/password")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeMyPassword(
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        userService.changeMyPassword(dto);
    }

    @Operation(
            summary = "Upload or update avatar",
            description = "Allows an authenticated user to upload or replace their profile avatar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Avatar saved",
                            content = @Content(schema = @Schema(implementation = FileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "File upload error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @RequestMapping(
            value = "/avatar",
            method = { RequestMethod.POST, RequestMethod.PUT },
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public FileResponse uploadMyAvatar(
            @Parameter(description = "Image file (jpg, png, etc.)", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return fileService.uploadMyAvatar(file);
    }

    @Operation(
            summary = "Delete avatar",
            description = "Allows an authenticated user to delete their current avatar.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Avatar deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Avatar not found")
            }
    )
    @DeleteMapping("/avatar")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyAvatar() {
        fileService.deleteMyAvatar();
    }

    @Operation(
            summary = "Get all notifications",
            description = "Get all notifications for the current user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public ResponseEntity<List<NotificationDto>> getMyNotifications() {
        // This would need to be implemented in NotificationService
        return ResponseEntity.ok(List.of());
    }

    @Operation(
            summary = "Clear all notifications",
            description = "Mark all notifications as read for the current user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notifications cleared successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @DeleteMapping("/notifications")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearMyNotifications() {
        // This would need to be implemented in NotificationService
    }

    @Operation(
            summary = "Get current balance",
            description = "Get the current balance for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public ResponseEntity<Map<String, BigDecimal>> getMyBalance() {
        var user = userService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "balance", user.getBalance(),
                "available", user.getAvailable(),
                "reserved", user.getReserved()
        ));
    }

    @Operation(
            summary = "Get call history",
            description = "Get all calls history for the current user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Call history retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/calls")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public ResponseEntity<List<CallDto>> getMyCallHistory() {
        var user = userService.getCurrentUser();
        List<CallDto> calls = callService.getCallHistoryForUser(user.getUsername());
        return ResponseEntity.ok(calls);
    }
}
