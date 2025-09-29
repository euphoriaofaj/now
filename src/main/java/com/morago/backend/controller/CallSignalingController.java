package com.morago.backend.controller;

import com.morago.backend.dto.call.CallActionRequest;
import com.morago.backend.service.call.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class CallSignalingController {

    @Autowired
    private CallService callService;

    @MessageMapping("/call.accept")
    public void acceptCall(@Payload CallActionRequest request,
                             SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        callService.acceptCall(request.getCallId(), username);
    }

    @MessageMapping("/call.reject")
    public void rejectCall(@Payload CallActionRequest request,
                           SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        callService.rejectCall(request.getCallId(), username);
    }

    @MessageMapping("/call.end")
    public void endCall(@Payload CallActionRequest request,
                           SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        callService.endCall(request.getCallId(), username);
    }

    @MessageMapping("/call.initiate")
    public void initiateCall(@Payload CallActionRequest request,
                        SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        callService.initiateCall(request.getTranslatorId(), request.getThemeId(), username);
    }

    @MessageMapping("/call.signal/{callId}")
    public void handleSignaling(@DestinationVariable String callId,
                                @Payload Object signalData,
                                SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        callService.handleCallSignaling(callId, signalData, username);
    }
}