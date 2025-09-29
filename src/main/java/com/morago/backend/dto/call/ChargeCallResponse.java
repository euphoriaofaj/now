package com.morago.backend.dto.call;

import java.math.BigDecimal;

public record ChargeCallResponse(
        Long clientId,
        BigDecimal clientBalanceAfter,
        Long interpreterId,
        BigDecimal interpreterBalanceAfter
) {}