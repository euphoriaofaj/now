package com.morago.backend.dto.translator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorResponseDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
