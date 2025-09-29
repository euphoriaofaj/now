package com.morago.backend.service.call;

import com.morago.backend.dto.call.CallDto;
import java.util.List;

public interface CallService {
    CallDto createCall(CallDto dto);
    CallDto getCallById(Long id);
    List<CallDto> getAllCalls();
    CallDto updateCall(Long id, CallDto dto);
    void deleteCall(Long id);
}
