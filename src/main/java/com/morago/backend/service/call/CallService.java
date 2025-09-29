package com.morago.backend.service.call;

import com.morago.backend.dto.call.CallDto;
import java.util.List;

public interface CallService {
    CallDto createCall(CallDto dto);
    CallDto getCallById(Long id);
    List<CallDto> getAllCalls();
    CallDto updateCall(Long id, CallDto dto);
    void deleteCall(Long id);
    
    // WebSocket call management
    void initiateCall(Long translatorId, Long themeId, String callerUsername);
    void acceptCall(String callId, String translatorUsername);
    void rejectCall(String callId, String translatorUsername);
    void endCall(String callId, String username);
    void handleCallSignaling(String callId, Object signalData, String username);
    
    // Call history
    List<CallDto> getCallHistoryForUser(String username);
}
