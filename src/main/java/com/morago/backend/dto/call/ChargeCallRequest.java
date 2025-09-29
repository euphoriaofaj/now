package com.morago.backend.dto.call;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChargeCallRequest(
        @NotNull Long clientId,
        @NotNull Long interpreterId,
        @NotBlank String callId,
        @NotNull BigDecimal wonAmount
) {}