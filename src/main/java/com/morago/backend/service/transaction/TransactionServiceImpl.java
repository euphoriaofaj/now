package com.morago.backend.service.transaction;

import com.morago.backend.entity.Money;
import com.morago.backend.entity.Transaction;
import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import com.morago.backend.exception.TransactionNotFoundException;
import com.morago.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository txnRepo;

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorrelationId(String correlationId) {
        return correlationId != null && txnRepo.existsByCorrelationId(correlationId);
    }

    @Override
    @Transactional
    public Long log(User user,
                    TransactionType type,
                    BigDecimal amount,
                    BigDecimal before,
                    BigDecimal after,
                    String correlationId,
                    String description,
                    EStatus status) {

        BigDecimal amt    = Money.s2(amount);
        BigDecimal beforeS= Money.s2(before);
        BigDecimal afterS = Money.s2(after);

        Transaction t = Transaction.builder()
                .user(user)
                .type(type)
                .amount(amt)
                .beforeBalance(beforeS)
                .afterBalance(afterS)
                .correlationId(correlationId)
                .description(description)
                .status(status == null ? EStatus.SUCCESSFUL : status)
                .build();

        return txnRepo.save(t).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> history(Long userId, Pageable pageable) {
        return txnRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction findByIdOrThrow(Long id) {
        return txnRepo.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }
}
