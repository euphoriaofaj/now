package com.morago.backend.controller;

import com.morago.backend.dto.tokens.WebRTCSignalMessage;
import com.morago.backend.service.webrtc.WebRTCService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.DestinationVariable;import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class WebRTCController {

    private final WebRTCService webRTCService;

    @MessageMapping("/webrtc.offer")
    public void handleOffer(@Payload WebRTCSignalMessage signal,
                            SimpMessageHeaderAccessor headerAccessor) {
        String fromUserId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : signal.getFromUserId();

        signal.setFromUserId(fromUserId);
        signal.setTimestamp(LocalDateTime.now());

        webRTCService.handleOffer(signal);
    }

    @MessageMapping("/webrtc.answer")
    public void handleAnswer(@Payload WebRTCSignalMessage signal,
                             SimpMessageHeaderAccessor headerAccessor) {
        String fromUserId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : signal.getFromUserId();

        signal.setFromUserId(fromUserId);
        signal.setTimestamp(LocalDateTime.now());

        webRTCService.handleAnswer(signal);
    }

    @MessageMapping("/webrtc.ice")
    public void handleIceCandidate(@Payload WebRTCSignalMessage signal,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String fromUserId = headerAccessor.getUser() != null ?
                headerAccessor.getUser().getName() : signal.getFromUserId();

        signal.setFromUserId(fromUserId);
        signal.setTimestamp(LocalDateTime.now());

        webRTCService.handleIceCandidate(signal);
    }
}