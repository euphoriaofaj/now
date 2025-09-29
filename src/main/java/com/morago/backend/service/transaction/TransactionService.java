package com.morago.backend.service.transaction;

import com.morago.backend.entity.Transaction;
import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {
    boolean existsByCorrelationId(String correlationId);

    Long log(User user,
             TransactionType type,
             BigDecimal amount,
             BigDecimal before,
             BigDecimal after,
             String correlationId,
             String description,
             EStatus status);

    Page<Transaction> history(Long userId, Pageable pageable);
    Transaction findByIdOrThrow(Long id);
}
