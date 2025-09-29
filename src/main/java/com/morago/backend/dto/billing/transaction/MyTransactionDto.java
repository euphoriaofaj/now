package com.morago.backend.dto.billing.transaction;

import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MyTransactionDto(
        Long id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,       // afterBalance
        String description,
        String correlationId,
        EStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {}