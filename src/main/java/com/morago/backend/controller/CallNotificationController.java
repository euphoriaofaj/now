package com.morago.backend.controller;

import com.morago.backend.dto.tokens.CallNotificationMessage;
import com.morago.backend.dto.tokens.WebRTCSignalMessage;
import com.morago.backend.service.call.CallNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CallNotificationController {

    private final CallNotificationService callNotificationService;

    @MessageMapping("/call.notify.incoming")
    public void notifyIncomingCall(@Payload CallNotificationMessage message,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String callerId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : message.getCallerId();

        message.setCallerId(callerId);
        message.setType("INCOMING_CALL");
        message.setTimestamp(LocalDateTime.now());

        callNotificationService.notifyIncomingCall(message);
    }

    @MessageMapping("/call.notify.end")
    public void notifyCallEnd(@Payload CallNotificationMessage message,
                              SimpMessageHeaderAccessor headerAccessor) {
        message.setType("CALL_ENDED");
        message.setTimestamp(LocalDateTime.now());

        callNotificationService.notifyCallEnd(message);
    }

    @MessageMapping("/call.notify.cancel")
    public void notifyCallCancel(@Payload CallNotificationMessage message,
                                 SimpMessageHeaderAccessor headerAccessor) {
        message.setType("CALL_CANCELLED");
        message.setTimestamp(LocalDateTime.now());

        callNotificationService.notifyCallCancel(message);
    }

    @MessageMapping("/webrtc.signal/{callId}")
    public void handleWebRTCSignal(@DestinationVariable String callId,
                                   @Payload WebRTCSignalMessage signal,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String fromUserId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : signal.getFromUserId();

        signal.setCallId(callId);
        signal.setFromUserId(fromUserId);
        signal.setTimestamp(LocalDateTime.now());

        callNotificationService.handleWebRTCSignal(signal);
    }
}