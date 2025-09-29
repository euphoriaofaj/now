package com.morago.backend.dto.billing.withdrawal;

import jakarta.validation.constraints.NotNull;

public record WithdrawalDecisionRequest(
        @NotNull Boolean approve,
        String adminNote
) {}
