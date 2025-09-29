package com.morago.backend.mapper;

import com.morago.backend.dto.user.UserProfileDto;
import com.morago.backend.entity.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileDto toDto(UserProfile userProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserProfile toEntity(UserProfileDto dto);

    @BeanMapping(ignoreByDefault = true)
    void updateEntityFromDto(UserProfileDto dto, @MappingTarget UserProfile entity);
}
