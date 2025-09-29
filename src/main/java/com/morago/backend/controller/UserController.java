package com.morago.backend.controller;

import com.morago.backend.dto.FileResponse;
import com.morago.backend.dto.password.ChangePasswordRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileRequestDto;
import com.morago.backend.dto.user.UserUpdateProfileResponseDto;
import com.morago.backend.service.file.FileService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

}
