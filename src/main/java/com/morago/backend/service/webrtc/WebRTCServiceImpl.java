package com.morago.backend.service.webrtc;

import com.morago.backend.dto.tokens.WebRTCSignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCServiceImpl implements WebRTCService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Map<String, Object>> activePeerConnections = new ConcurrentHashMap<>();

    @Override
    public void initializePeerConnection(String callId, String userId) {
        log.info("Initializing peer connection for callId: {}, userId: {}", callId, userId);

        String connectionKey = callId + ":" + userId;
        Map<String, Object> connectionData = new ConcurrentHashMap<>();
        connectionData.put("callId", callId);
        connectionData.put("userId", userId);
        connectionData.put("status", "initialized");
        connectionData.put("createdAt", LocalDateTime.now());

        activePeerConnections.put(connectionKey, connectionData);

        WebRTCSignalMessage initMessage = WebRTCSignalMessage.builder()
                .callId(callId)
                .fromUserId(userId)
                .type("PEER_INITIALIZED")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/webrtc-room/" + callId, initMessage);
    }

    @Override
    public void handleOffer(WebRTCSignalMessage signal) {
        log.info("Handling WebRTC offer from {} to {} for call {}",
                signal.getFromUserId(), signal.getToUserId(), signal.getCallId());

        signal.setType("OFFER");
        signal.setTimestamp(LocalDateTime.now());

        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/webrtc-room/" + signal.getCallId(),
                signal
        );
    }

    @Override
    public void handleAnswer(WebRTCSignalMessage signal) {
        log.info("Handling WebRTC answer from {} to {} for call {}",
                signal.getFromUserId(), signal.getToUserId(), signal.getCallId());

        signal.setType("ANSWER");
        signal.setTimestamp(LocalDateTime.now());

        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/webrtc-room/" + signal.getCallId(),
                signal
        );
    }

    @Override
    public void handleIceCandidate(WebRTCSignalMessage signal) {
        log.info("Handling ICE candidate from {} for call {}",
                signal.getFromUserId(), signal.getCallId());

        signal.setType("ICE_CANDIDATE");
        signal.setTimestamp(LocalDateTime.now());

        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/webrtc-room/" + signal.getCallId(),
                signal
        );
    }

    @Override
    public void closePeerConnection(String callId, String userId) {
        log.info("Closing peer connection for callId: {}, userId: {}", callId, userId);

        String connectionKey = callId + ":" + userId;
        activePeerConnections.remove(connectionKey);

        WebRTCSignalMessage closeMessage = WebRTCSignalMessage.builder()
                .callId(callId)
                .fromUserId(userId)
                .type("PEER_DISCONNECTED")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/webrtc-room/" + callId, closeMessage);
    }
}