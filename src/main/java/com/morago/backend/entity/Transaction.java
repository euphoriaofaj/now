package com.morago.backend.entity;

import com.morago.backend.config.jpa.BigDecimalScale2Converter;
import com.morago.backend.entity.enumFiles.EStatus;
import com.morago.backend.entity.enumFiles.TransactionType;
import com.morago.backend.listener.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions",
        indexes = {@Index(name="idx_txn_user", columnList="user_id"),
                @Index(name="idx_txn_corr", columnList="correlation_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private TransactionType type;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    @Convert(converter = BigDecimalScale2Converter.class)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private EStatus status = EStatus.SUCCESSFUL;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    @Column(name = "before_balance", precision = 19, scale = 2, nullable = false)
    @Convert(converter = BigDecimalScale2Converter.class)
    private BigDecimal beforeBalance;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    @Column(name = "after_balance", precision = 19, scale = 2, nullable = false)
    @Convert(converter = BigDecimalScale2Converter.class)
    private BigDecimal afterBalance;

    @Column(name = "description", length = 500)
    private String description;
}

