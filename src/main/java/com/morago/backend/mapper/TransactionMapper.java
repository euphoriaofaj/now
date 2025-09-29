package com.morago.backend.mapper;

import com.morago.backend.dto.billing.transaction.MyTransactionDto;
import com.morago.backend.dto.billing.transaction.TransactionAdminDto;
import com.morago.backend.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "balanceAfter", source = "afterBalance")
    @Mapping(target = "createdAt", source = "createdAt")
    MyTransactionDto toMyDto(Transaction t);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "createdAt")
    TransactionAdminDto toAdminDto(Transaction t);
}

