package com.morago.backend.mapper;

import com.morago.backend.dto.billing.deposit.DepositDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface DepositMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "wonAmount", source = "wonDecimal")
    DepositDto toDto(com.morago.backend.entity.Deposit d);
}


