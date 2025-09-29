package com.morago.backend.controller;

import com.morago.backend.dto.billing.deposit.DepositConfirmResponse;
import com.morago.backend.dto.billing.deposit.DepositDto;
import com.morago.backend.dto.call.ChargeCallRequest;
import com.morago.backend.dto.billing.deposit.CreateDepositRequest;
import com.morago.backend.mapper.DepositMapper;
import com.morago.backend.service.deposit.DepositService;
import com.morago.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Billing")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final DepositService depositService;
    private final UserService userService;
    private final DepositMapper depositMapper;

    @PostMapping("/deposits")
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR')")
    public ResponseEntity<DepositDto> createDeposit(@RequestBody @Valid CreateDepositRequest req) {
        Long me = userService.getCurrentUserId();
        Long id = depositService.createDeposit(me, req.accountHolder(), req.nameOfBank(), req.wonAmount());
        var dep = depositService.findByIdOrThrow(id);
        return ResponseEntity.ok(depositMapper.toDto(dep));
    }

    @PostMapping("/deposits/{depositId}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepositConfirmResponse> confirmDeposit(@PathVariable Long depositId) {
        depositService.confirmDeposit(depositId);

        var dep = depositService.findByIdOrThrow(depositId);
        var user = userService.findByIdOrThrow(dep.getUser().getId());

        var body = new DepositConfirmResponse(
                dep.getId(),
                user.getId(),
                dep.getWonDecimal(),
                dep.getStatus(),
                user.getBalance(),
                "deposit:" + dep.getId(),
                dep.getUpdatedAt() != null ? dep.getUpdatedAt() : dep.getCreatedAt()
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/charge-call")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void chargeCall(@RequestBody @Valid ChargeCallRequest dto) {
        depositService.chargeCallAndPay(dto.clientId(), dto.interpreterId(), dto.callId(), dto.wonAmount());
    }
}