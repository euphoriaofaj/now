package com.morago.backend.service.call;

import com.morago.backend.dto.tokens.CallNotificationMessage;
import com.morago.backend.dto.tokens.WebRTCSignalMessage;

public interface CallNotificationService {
    void notifyIncomingCall(CallNotificationMessage message);
    void notifyCallEnd(CallNotificationMessage message);
    void notifyCallCancel(CallNotificationMessage message);
    void handleWebRTCSignal(WebRTCSignalMessage signal);
    void notifyTranslatorsForCall(CallNotificationMessage message);
}