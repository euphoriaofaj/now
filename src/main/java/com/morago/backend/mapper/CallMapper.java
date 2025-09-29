package com.morago.backend.mapper;

import com.morago.backend.config.GlobalMappingConfig;
import com.morago.backend.dto.call.CallDto;
import com.morago.backend.entity.Call;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",config = GlobalMappingConfig.class, uses = {MoneyMapper.class, RefMappers.class})
public interface CallMapper {

    CallMapper INSTANCE = Mappers.getMapper(CallMapper.class);

    @Mapping(target = "callerId", source = "caller.id")
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "themeId", source = "theme.id")
    CallDto toDto(Call call);

    /* ---------- Entity <- DTO (create/replace) ---------- */
    @Mapping(target = "caller",    source = "callerId")
    @Mapping(target = "recipient", source = "recipientId")
    @Mapping(target = "theme",     source = "themeId")
    @Mapping(target = "sumDecimal",  qualifiedByName = "scale2")
    @Mapping(target = "commission",  qualifiedByName = "scale2")
    Call toEntity(CallDto dto);

    @AfterMapping
    default void normalizeMoney(@MappingTarget Call e, @Context MoneyMapper money) {
        e.setSumDecimal(money.scale2(e.getSumDecimal()));
        e.setCommission(money.scale2(e.getCommission()));
    }
}
