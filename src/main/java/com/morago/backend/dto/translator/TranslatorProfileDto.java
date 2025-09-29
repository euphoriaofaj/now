package com.morago.backend.dto.translator;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TranslatorProfileDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDate dateOfBirth;
    private String email;
    private Boolean isVerified;
    private Boolean isOnline;
    private String levelOfKorean;
    private LocalDateTime updatedAt;
    private String avatarUrl;



    private Long userId;

    private Set<Long> languageIds;
    private Set<Long> themeIds;

    private BigDecimal ratingAvg;   // 0.00..5.00 (scale=2)
    private Integer    ratingCount;
}