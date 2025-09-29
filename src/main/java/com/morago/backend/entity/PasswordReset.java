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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_resets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordReset extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name ="token", unique = true, nullable = false, length = 64)
    private String token;

    @Column(name ="phone", length = 100, nullable = false)
    private String phone;

    @Column(name ="reset_code", nullable = false)
    private Integer resetCode;

    @Column(name = "expires_at", nullable = false)
    private java.time.LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private Boolean used = false;
}
