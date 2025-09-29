package com.morago.backend.entity;

import com.morago.backend.listener.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "debtors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debtor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    @Column(name = "account_holder", length = 200)
    private String accountHolder;

    @NotBlank
    @Column(name = "name_of_bank", length = 200)
    private String nameOfBank;

    @Column(name = "is_paid")
    @Builder.Default
    private boolean isPaid = false;
}