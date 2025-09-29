package com.morago.backend.dto.call;

import lombok.Data;

@Data
public class CallActionRequest {
    private String callId;
    private Long translatorId;
    private Long themeId;
    private String reason;
}