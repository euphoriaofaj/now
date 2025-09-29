package com.morago.backend.config.websocket;

import com.morago.backend.service.call.CallAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final CallAccessService callAccessService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
            
            if (destination != null && username != null) {
                // Check call-related destinations
                if (destination.startsWith("/app/call.") || destination.startsWith("/app/webrtc.")) {
                    if (!callAccessService.canAccessCallEndpoint(username, destination, message)) {
                        log.warn("Access denied for user {} to destination {}", username, destination);
                        return null; // Block the message
                    }
                }
            }
        }
        
        return message;
    }
}