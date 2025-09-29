package com.morago.backend.repository;

import com.morago.backend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByCorrelationId(String correlationId);
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
