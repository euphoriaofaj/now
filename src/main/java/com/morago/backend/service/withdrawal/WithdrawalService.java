package com.morago.backend.service.withdrawal;

import com.morago.backend.entity.Withdrawal;

import java.math.BigDecimal;

public interface WithdrawalService {
    Long requestWithdrawal(Long userId, String accountNumber, String holder, String bank, BigDecimal wonAmount);
    void decideWithdrawal(Long withdrawalId, boolean approve, String adminNote);

    Withdrawal findByIdOrThrow(Long id);
}
