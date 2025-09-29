package com.morago.backend.dto.tokens;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallNotificationMessage {
    private String id;
    private String callId;
    private String callerId;
    private String callerName;
    private String recipientId;
    private String translatorId;
    private String type;
    private String status;
    private Long themeId;
    private String themeName;
    private String channelName;
    private LocalDateTime timestamp;
    private Object webrtcData; // For WebRTC signaling data
    private String message;
}
