package com.morago.backend.dto.tokens;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebRTCSignalMessage {
    private String callId;
    private String fromUserId;
    private String toUserId;
    private String type;
    private Object sdp; // Session Description Protocol data
    private Object candidate;
    private LocalDateTime timestamp;
    private String channelName;
}