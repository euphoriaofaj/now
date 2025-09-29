package com.morago.backend.service.call;

import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallAccessService {

    private final UserRepository userRepository;

    public boolean canAccessCallEndpoint(String username, String destination, Message<?> message) {
        try {
            // Check if user exists and get their roles
            var user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                log.warn("User not found: {}", username);
                return false;
            }

            boolean isUser = user.get().getRoles().stream()
                    .anyMatch(role -> role.getName() == Roles.ROLE_USER);
            boolean isTranslator = user.get().getRoles().stream()
                    .anyMatch(role -> role.getName() == Roles.ROLE_TRANSLATOR);

            // Call initiation - only USER can initiate calls
            if (destination.equals("/app/call.initiate")) {
                return isUser;
            }

            // Call responses - only TRANSLATOR can accept/reject
            if (destination.equals("/app/call.accept") || destination.equals("/app/call.reject")) {
                return isTranslator;
            }

            // Call end - both USER and TRANSLATOR can end calls
            if (destination.equals("/app/call.end")) {
                return isUser || isTranslator;
            }

            // WebRTC signaling - both can participate once call is established
            if (destination.startsWith("/app/webrtc.")) {
                return isUser || isTranslator;
            }

            // Other call-related endpoints
            if (destination.startsWith("/app/call.")) {
                return isUser || isTranslator;
            }

            return true; // Allow other destinations
        } catch (Exception e) {
            log.error("Error checking call access for user {}: {}", username, e.getMessage());
            return false;
        }
    }
}