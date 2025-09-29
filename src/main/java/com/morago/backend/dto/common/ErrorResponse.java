package com.morago.backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String code;
    private String path;
    private Instant timestamp;
    private Map<String, String> errors;
}
