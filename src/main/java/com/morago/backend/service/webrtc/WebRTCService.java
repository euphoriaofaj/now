package com.morago.backend.service.webrtc;

import com.morago.backend.dto.tokens.WebRTCSignalMessage;

public interface WebRTCService {
    void initializePeerConnection(String callId, String userId);
    void handleOffer(WebRTCSignalMessage signal);
    void handleAnswer(WebRTCSignalMessage signal);
    void handleIceCandidate(WebRTCSignalMessage signal);
    void closePeerConnection(String callId, String userId);
}