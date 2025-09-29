package com.morago.backend.entity;


import com.morago.backend.config.jpa.BigDecimalScale2Converter;
import com.morago.backend.entity.enumFiles.EStatus;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposit extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "account_holder", length = 200, nullable = false)
    private String accountHolder;

    @NotBlank
    @Column(name = "name_of_bank", length = 200, nullable = false)
    private String nameOfBank;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "coin_decimal", precision = 19, scale = 2, nullable = false)
    @Convert(converter = BigDecimalScale2Converter.class)
    private BigDecimal coinDecimal;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "won_decimal", precision = 19, scale = 2, nullable = false)
    @Convert(converter = BigDecimalScale2Converter.class)
    private BigDecimal wonDecimal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private EStatus status = EStatus.PENDING;
}