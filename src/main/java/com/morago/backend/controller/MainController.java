package com.morago.backend.controller;

import com.morago.backend.dto.tokens.JWTRequest;
import com.morago.backend.dto.tokens.JWTResponse;
import com.morago.backend.dto.tokens.RefreshTokenRequest;
import com.morago.backend.dto.user.UserRegistrationRequestDto;
import com.morago.backend.dto.user.UserRegistrationResponseDto;
import com.morago.backend.service.auth.AuthService;
import com.morago.backend.service.token.RefreshTokenService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "User authentication and token management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MainController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Operation(
            summary = "Log in with phone and password",
            description = "Authenticates the user and returns a JWT access token and refresh token. " +
                    "Phone format: 01012345678",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = JWTRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\"username\":\"01012345678\",\"password\":\"P@ssw0rd!\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication",
                            content = @Content(schema = @Schema(implementation = JWTResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid username or password")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody JWTRequest authRequest) {
        JWTResponse response = authService.createAuthToken(authRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\"refreshToken\":\"<your_refresh_token>\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "New access token generated",
                            content = @Content(schema = @Schema(implementation = JWTResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<JWTResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        JWTResponse jwtResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(
            summary = "Register user",
            description = "Registers a new user with role ROLE_USER. Phone format strictly 01012345678.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(schema = @Schema(implementation = UserRegistrationResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Phone already registered")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDto> registerUser(@Valid @RequestBody UserRegistrationRequestDto dto) {
        return ResponseEntity.status(201).body(userService.registerUser(dto));
    }

    @Operation(
            summary = "Register translator",
            description = "Registers a new translator with role ROLE_TRANSLATOR. Phone format 01012345678.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Translator created",
                            content = @Content(schema = @Schema(implementation = UserRegistrationResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Phone already registered")
            }
    )
    @PostMapping("/register/translator")
    public ResponseEntity<UserRegistrationResponseDto> registerTranslator(@Valid @RequestBody UserRegistrationRequestDto dto) {
        return ResponseEntity.status(201).body(userService.registerTranslator(dto));
    }

    @Operation(
            summary = "Log out",
            description = "Invalidates refresh tokens. Requires a valid Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = {
                                    @ExampleObject(name = "Delete specific token",
                                            value = "{\"refreshToken\":\"<user_refresh_token>\"}"),
                                    @ExampleObject(name = "Delete all tokens (no body)", value = "")
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Logged out (no content)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody(required = false) RefreshTokenRequest request,
                       Authentication auth) {
        String username = auth.getName();
        String providedToken = (request != null) ? request.getRefreshToken() : null;
        refreshTokenService.logout(username, providedToken);
    }
}
