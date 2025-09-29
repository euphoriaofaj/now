package com.morago.backend.controller;

import com.morago.backend.dto.translator.TranslatorProfileDto;
import com.morago.backend.mapper.TranslatorProfileMapper;
import com.morago.backend.service.profile.TranslatorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/translators")
@RequiredArgsConstructor
@Tag(name = "User - Translators", description = "User endpoints for finding translators")
public class UserTranslatorController {

    private final TranslatorProfileService translatorProfileService;
    private final TranslatorProfileMapper translatorProfileMapper;

    @Operation(
            summary = "Get online translators",
            description = "Get list of currently online and verified translators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Online translators retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/online")
    @PreAuthorize("hasAnyRole('USER', 'TRANSLATOR')")
    public ResponseEntity<List<TranslatorProfileDto>> getOnlineTranslators() {
        return ResponseEntity.ok(translatorProfileService.getOnlineTranslators());
    }

    @Operation(
            summary = "Get translators by theme",
            description = "Get translators who handle a specific theme.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Translators retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/by-theme/{themeId}")
    @PreAuthorize("hasAnyRole('USER', 'TRANSLATOR')")
    public ResponseEntity<List<TranslatorProfileDto>> getTranslatorsByTheme(@PathVariable Long themeId) {
        return ResponseEntity.ok(translatorProfileService.getTranslatorsByTheme(themeId));
    }
}
