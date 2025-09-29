package com.morago.backend.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class TransactionNotFoundException extends ApiException {

    public TransactionNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND,
                "Transaction not found: " + id,
                "TRANSACTION_NOT_FOUND",
                null);
    }

    public TransactionNotFoundException(String correlationId) {
        super(HttpStatus.NOT_FOUND,
                "Transaction not found by correlationId: " + correlationId,
                "TRANSACTION_NOT_FOUND",
                Map.of("correlationId", correlationId));
    }
}