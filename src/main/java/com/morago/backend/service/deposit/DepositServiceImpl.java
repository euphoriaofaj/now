package com.morago.backend.service.deposit;

import com.morago.backend.entity.Deposit;
import com.morago.backend.entity.Money;
import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import com.morago.backend.exception.DepositNotFoundException;
import com.morago.backend.repository.DepositRepository;
import com.morago.backend.service.transaction.TransactionService;
import com.morago.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService{
    private final DepositRepository depositRepo;
    private final UserService userService;
    private final TransactionService txnService;

    @Override
    @Transactional
    public Long createDeposit(Long userId, String accountHolder, String nameOfBank, BigDecimal wonAmount) {
        BigDecimal amt = Money.s2(wonAmount);
        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        User user = userService.findByIdOrThrow(userId);

        Deposit dep = Deposit.builder()
                .user(user)
                .accountHolder(accountHolder)
                .nameOfBank(nameOfBank)
                .wonDecimal(amt)
                .coinDecimal(BigDecimal.ZERO)
                .status(EStatus.PENDING)
                .build();

        return depositRepo.save(dep).getId();
    }

    @Override
    @Transactional
    public void confirmDeposit(Long depositId) {
        Deposit dep = depositRepo.findByIdForUpdate(depositId)
                .orElseThrow(() -> new DepositNotFoundException(depositId));

        if (dep.getStatus() == EStatus.SUCCESSFUL) {
            log.info("[DEPOSIT] Deposit {} already confirmed, skipping", depositId);
            return;
        }

        BigDecimal amt = Money.s2(dep.getWonDecimal());
        if (amt.signum() <= 0) throw new IllegalArgumentException("Amount must be > 0");

        User user = userService.findByIdForUpdateOrThrow(dep.getUser().getId());

        BigDecimal before = user.getBalance();
        user.setBalance(before.add(amt));

        String corr = "deposit:" + dep.getId();
        try {
            txnService.log(user, TransactionType.DEPOSIT, amt, before, user.getBalance(),
                    corr, "Deposit confirmed", EStatus.SUCCESSFUL);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("[DEPOSIT] Duplicate transaction for depositId={}, corrId={}", depositId, corr);
        }

        dep.setStatus(EStatus.SUCCESSFUL);
        log.info("[DEPOSIT] Confirmed depositId={} userId={} amount={} newBalance={}",
                depositId, user.getId(), amt, user.getBalance());
    }

    @Override
    @Transactional
    public void chargeCallAndPay(Long clientId, Long interpreterId, String callId, BigDecimal wonAmount) {
        if (txnService.existsByCorrelationId(callId)) {
            log.warn("[CALL] Duplicate call charge detected, callId={}", callId);
            return;
        }

        BigDecimal amt = Money.s2(wonAmount);
        if (amt.signum() <= 0) throw new IllegalArgumentException("Amount must be > 0");

        Long firstId = clientId < interpreterId ? clientId : interpreterId;
        Long secondId = clientId < interpreterId ? interpreterId : clientId;

        User first = userService.findByIdForUpdateOrThrow(firstId);
        User second = userService.findByIdForUpdateOrThrow(secondId);

        User client = first.getId().equals(clientId) ? first : second;
        User interp  = first.getId().equals(interpreterId) ? first : second;

        if (client.getAvailable().compareTo(amt) < 0) {
            log.error("[CALL] Insufficient funds clientId={} balance={} required={}",
                    clientId, client.getAvailable(), amt);
            throw new IllegalStateException("Insufficient funds");
        }

        BigDecimal cBefore = client.getBalance();
        client.setBalance(cBefore.subtract(amt));
        txnService.log(client, TransactionType.CALL_DEBIT, amt, cBefore, client.getBalance(),
                callId, "Call charge", EStatus.SUCCESSFUL);

        BigDecimal iBefore = interp.getBalance();
        interp.setBalance(iBefore.add(amt));
        txnService.log(interp, TransactionType.CALL_CREDIT, amt, iBefore, interp.getBalance(),
                callId, "Call payout", EStatus.SUCCESSFUL);

        log.info("[CALL] Charged clientId={} interpId={} callId={} amount={} clientNewBalance={} interpNewBalance={}",
                clientId, interpreterId, callId, amt, client.getBalance(), interp.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public Deposit findByIdOrThrow(Long id) {
        return depositRepo.findById(id)
                .orElseThrow(() -> new DepositNotFoundException(id));
    }
}