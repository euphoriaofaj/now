package com.morago.backend.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
    private Money() {}
    public static BigDecimal s2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
