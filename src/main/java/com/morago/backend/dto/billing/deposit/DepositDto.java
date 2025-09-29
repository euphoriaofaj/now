package com.morago.backend.dto.billing.deposit;

public record DepositDto(
        Long id,
        Long userId,
        java.math.BigDecimal wonAmount,
        com.morago.backend.entity.enumFiles.EStatus status,
        java.time.LocalDateTime createdAt
) {}