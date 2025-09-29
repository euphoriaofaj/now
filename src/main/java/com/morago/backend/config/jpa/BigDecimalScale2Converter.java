package com.morago.backend.config.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Converter(autoApply = false)
public class BigDecimalScale2Converter implements AttributeConverter<BigDecimal, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(BigDecimal value) {
        return normalize(value);
    }

    @Override
    public BigDecimal convertToEntityAttribute(BigDecimal value) {
        return normalize(value);
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
