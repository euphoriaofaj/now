package com.morago.backend.entity;

import com.morago.backend.entity.enumFiles.CallStatus;
import com.morago.backend.listener.Auditable;
import jakarta.persistence.Column;
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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "calls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Call extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Column(name = "duration", nullable = false)
    @Builder.Default
    @PositiveOrZero
    private int duration = 0;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private boolean status = false;

    @DecimalMin("0.00")
    @Column(name = "sum_decimal", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal sumDecimal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @DecimalMin("0.00")
    @Column(name = "commission", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal commission = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Column(name = "translator_has_joined", nullable = false)
    @Builder.Default
    private boolean translatorHasJoined = false;

    @Column(name = "user_has_rated", nullable = false)
    @Builder.Default
    private boolean userHasRated = false;

    @Column(name = "channel_name", length = 50)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_status", length = 40, nullable = false)
    @Builder.Default
    private CallStatus callStatus = CallStatus.CONNECT_NOT_SET;

    @Column(name = "is_end_call")
    @Builder.Default
    private boolean endCall = false;

    public boolean isEndCall() { return endCall; }      // опционально, если нужно именно такой геттер
    public void setEndCall(boolean v) { this.endCall = v; }
}

