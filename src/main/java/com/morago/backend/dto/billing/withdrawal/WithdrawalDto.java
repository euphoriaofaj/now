package com.morago.backend.dto.billing.withdrawal;

import com.morago.backend.entity.enumFiles.EStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawalDto(
        Long id,
        Long userId,
        BigDecimal amount,
        EStatus status,
        String accountNumber,
        String accountHolder,
        String nameOfBank,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {}