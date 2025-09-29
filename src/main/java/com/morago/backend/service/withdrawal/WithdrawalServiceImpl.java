package com.morago.backend.service.withdrawal;

import com.morago.backend.entity.Money;
import com.morago.backend.entity.User;
import com.morago.backend.entity.Withdrawal;
import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import com.morago.backend.exception.WithdrawalNotFoundException;
import com.morago.backend.repository.WithdrawalRepository;
import com.morago.backend.service.transaction.TransactionService;
import com.morago.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawalServiceImpl implements WithdrawalService {
    private final WithdrawalRepository withdrawalRepo;
    private final UserService userService;
    private final TransactionService txnService;

    @Override
    public Long requestWithdrawal(Long userId, String accountNumber, String holder, String bank, BigDecimal wonAmount) {
        BigDecimal amt = Money.s2(wonAmount);
        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        User u = userService.findByIdOrThrow(userId);
        if (u.getAvailable().compareTo(amt) < 0) {
            throw new IllegalStateException("Insufficient available funds");
        }

        u.setReserved(u.getReserved().add(amt));

        txnService.log(
                u,
                TransactionType.WITHDRAW_REQUEST,
                amt,
                u.getBalance(),
                u.getBalance(),
                null,
                "Withdrawal requested (reserved)",
                EStatus.SUCCESSFUL
        );

        Withdrawal w = Withdrawal.builder()
                .user(u)
                .accountNumber(accountNumber)
                .accountHolder(holder)
                .nameOfBank(bank)
                .sumDecimal(amt)
                .status(EStatus.PENDING)
                .build();

        return withdrawalRepo.save(w).getId();
    }

    @Override
    public void decideWithdrawal(Long withdrawalId, boolean approve, String adminNote) {
        Withdrawal w = withdrawalRepo.findById(withdrawalId).orElseThrow();
        if (w.getStatus() != EStatus.PENDING) return;

        User u = userService.findByIdOrThrow(w.getUser().getId());
        BigDecimal amt = Money.s2(w.getSumDecimal());

        if (approve) {
            if (u.getReserved().compareTo(amt) < 0) {
                throw new IllegalStateException("Reserved < amount");
            }
            BigDecimal before = u.getBalance();
            u.setReserved(u.getReserved().subtract(amt));
            u.setBalance(u.getBalance().subtract(amt));

            txnService.log(
                    u,
                    TransactionType.WITHDRAW_APPROVE,
                    amt,
                    before,
                    u.getBalance(),
                    "withdraw:" + w.getId(),
                    "Withdrawal approved: " + adminNote,
                    EStatus.SUCCESSFUL
            );

            w.setStatus(EStatus.SUCCESSFUL);
        } else {
            if (u.getReserved().compareTo(amt) < 0) {
                throw new IllegalStateException("Reserved < amount");
            }
            BigDecimal before = u.getBalance();
            u.setReserved(u.getReserved().subtract(amt));

            txnService.log(
                    u,
                    TransactionType.WITHDRAW_REJECT,
                    amt,
                    before,
                    before,
                    "withdraw:" + w.getId(),
                    "Withdrawal rejected: " + adminNote,
                    EStatus.SUCCESSFUL
            );

            w.setStatus(EStatus.FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Withdrawal findByIdOrThrow(Long id) {
        return withdrawalRepo.findById(id)
                .orElseThrow(() -> new WithdrawalNotFoundException(id));
    }
}
