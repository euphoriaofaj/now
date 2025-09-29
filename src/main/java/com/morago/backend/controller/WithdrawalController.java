package com.morago.backend.controller;

import com.morago.backend.dto.billing.withdrawal.CreateWithdrawalRequest;
import com.morago.backend.dto.billing.withdrawal.WithdrawalDecisionRequest;
import com.morago.backend.mapper.WithdrawalMapper;
import com.morago.backend.service.withdrawal.WithdrawalService;
import com.morago.backend.dto.billing.withdrawal.WithdrawalDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Withdrawals")
@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {
    private final WithdrawalService withdrawalService;
    private final WithdrawalMapper withdrawalMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public ResponseEntity<WithdrawalDto> request(@RequestBody @Valid CreateWithdrawalRequest dto) {
        Long id = withdrawalService.requestWithdrawal(
                dto.userId(), dto.accountNumber(), dto.accountHolder(), dto.nameOfBank(), dto.wonAmount()
        );
        var w = withdrawalService.findByIdOrThrow(id);
        return ResponseEntity.ok(withdrawalMapper.toDto(w));
    }

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> decide(@PathVariable Long id, @RequestBody WithdrawalDecisionRequest dto) {
        withdrawalService.decideWithdrawal(id, dto.approve(), dto.adminNote());
        return ResponseEntity.noContent().build();
    }
}
