package com.morago.backend.mapper;

import com.morago.backend.dto.billing.withdrawal.WithdrawalDto;
import com.morago.backend.entity.Withdrawal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "amount", source = "sumDecimal")
    @Mapping(target = "createdAt", source = "createdAt")
    WithdrawalDto toDto(Withdrawal w);
}
