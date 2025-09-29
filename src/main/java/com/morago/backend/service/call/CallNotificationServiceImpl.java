package com.morago.backend.service.call;

import com.morago.backend.dto.tokens.CallNotificationMessage;
import com.morago.backend.dto.tokens.WebRTCSignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallNotificationServiceImpl implements CallNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyIncomingCall(CallNotificationMessage message) {
        log.info("Notifying incoming call: {} from {} to {}",
                message.getCallId(), message.getCallerId(), message.getRecipientId());

        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/call-notifications",
                message
        );

        notifyTranslatorsForCall(message);
    }

    @Override
    public void notifyCallEnd(CallNotificationMessage message) {
        log.info("Notifying call end: {}", message.getCallId());

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + message.getCallId(),
                message
        );

        if (message.getCallerId() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getCallerId(),
                    "/queue/call-notifications",
                    message
            );
        }

        if (message.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/call-notifications",
                    message
            );
        }

        if (message.getTranslatorId() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getTranslatorId(),
                    "/queue/call-notifications",
                    message
            );
        }
    }

    @Override
    public void notifyCallCancel(CallNotificationMessage message) {
        log.info("Notifying call cancellation: {}", message.getCallId());

        messagingTemplate.convertAndSend(
                "/topic/call-room/" + message.getCallId(),
                message
        );

        if (message.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/call-notifications",
                    message
            );
        }
    }

    @Override
    public void handleWebRTCSignal(WebRTCSignalMessage signal) {
        log.info("Handling WebRTC signal: {} from {} to {}",
                signal.getType(), signal.getFromUserId(), signal.getToUserId());

        if (signal.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signal.getToUserId(),
                    "/queue/webrtc-signals",
                    signal
            );
        }

        if (signal.getCallId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/webrtc-room/" + signal.getCallId(),
                    signal
            );
        }
    }

    @Override
    public void notifyTranslatorsForCall(CallNotificationMessage message) {
        log.info("Notifying translators for call: {} with theme: {}",
                message.getCallId(), message.getThemeId());

        messagingTemplate.convertAndSend(
                "/topic/translator-calls/" + message.getThemeId(),
                message
        );

        messagingTemplate.convertAndSend(
                "/topic/translator-notifications",
                message
        );
    }
}