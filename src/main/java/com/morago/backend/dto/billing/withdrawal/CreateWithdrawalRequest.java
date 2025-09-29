package com.morago.backend.dto.billing.withdrawal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateWithdrawalRequest(
        @NotNull Long userId,
        @NotBlank String accountNumber,
        @NotBlank String accountHolder,
        @NotBlank String nameOfBank,
        @NotNull BigDecimal wonAmount
) {}