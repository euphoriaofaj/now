package com.morago.backend.exception;

import org.springframework.http.HttpStatus;

public class WithdrawalNotFoundException extends ApiException {
  public WithdrawalNotFoundException(Long id) {
    super(
            HttpStatus.NOT_FOUND,
            "Withdrawal not found: " + id,
            "WITHDRAWAL_NOT_FOUND",
            null
    );
  }
}