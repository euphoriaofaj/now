package com.morago.backend.exception;

import org.springframework.http.HttpStatus;

public class DepositNotFoundException extends ApiException {
    public DepositNotFoundException(Long id) {
        super(
                HttpStatus.NOT_FOUND,
                "Deposit not found: " + id,
                "DEPOSIT_NOT_FOUND",
                null
        );
    }
}
