package com.morago.backend.controller;

import com.morago.backend.dto.billing.transaction.MyTransactionDto;
import com.morago.backend.dto.billing.transaction.TransactionAdminDto;
import com.morago.backend.mapper.TransactionMapper;
import com.morago.backend.service.transaction.TransactionService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transactions")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService txnService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR','ADMIN')")
    public Page<MyTransactionDto> myHistory(Pageable pageable) {
        Long me = userService.getCurrentUserId();
        return txnService.history(me, pageable)
                .map(transactionMapper::toMyDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionAdminDto> historyByUser(@RequestParam Long userId, Pageable pageable) {
        return txnService.history(userId, pageable)
                .map(transactionMapper::toAdminDto);
    }
}
