package com.morago.backend.dto.billing.deposit;

import com.morago.backend.entity.enumFiles.EStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositConfirmResponse(
        Long depositId,
        Long userId,
        BigDecimal amount,
        EStatus status,
        BigDecimal balanceAfter,
        String correlationId,
        LocalDateTime confirmedAt
) {}