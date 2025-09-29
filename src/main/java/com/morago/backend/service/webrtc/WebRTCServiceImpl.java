package com.morago.backend.service.webrtc;

import com.morago.backend.dto.tokens.WebRTCSignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCServiceImpl implements WebRTCService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handleOffer(WebRTCSignalMessage signal) {
        log.info("Handling WebRTC offer from {} to {} for call {}",
                signal.getFromUserId(), signal.getToUserId(), signal.getCallId());

        // Send offer only to the intended recipient
        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }
    }

    @Override
    public void handleAnswer(WebRTCSignalMessage signal) {
        log.info("Handling WebRTC answer from {} to {} for call {}",
                signal.getFromUserId(), signal.getToUserId(), signal.getCallId());

        // Send answer only to the intended recipient
        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }
    }

    @Override
    public void handleIceCandidate(WebRTCSignalMessage signal) {
        log.info("Handling ICE candidate from {} for call {}",
                signal.getFromUserId(), signal.getCallId());

        // Send ICE candidate only to the intended recipient
        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }
    }
}