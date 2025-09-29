package com.morago.backend.service.webrtc;

import com.morago.backend.dto.tokens.WebRTCSignalMessage;

public interface WebRTCService {
    void handleOffer(WebRTCSignalMessage signal);
    void handleAnswer(WebRTCSignalMessage signal);
    void handleIceCandidate(WebRTCSignalMessage signal);
}