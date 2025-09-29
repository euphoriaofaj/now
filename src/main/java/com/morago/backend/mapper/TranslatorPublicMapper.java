package com.morago.backend.mapper;

import com.morago.backend.dto.translator.TranslatorProfileDto;
import com.morago.backend.entity.TranslatorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.stream.Collectors;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TranslatorPublicMapper {

    @Mapping(target = "languageIds",
            expression = "java(profile.getLanguages().stream().map(lang -> lang.getId()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "themeIds",
            expression = "java(profile.getThemes().stream().map(theme -> theme.getId()).collect(java.util.stream.Collectors.toSet()))")
    TranslatorProfileDto toDto(TranslatorProfile profile);

    List<TranslatorProfileDto> toDtoList(List<TranslatorProfile> profiles);
}
