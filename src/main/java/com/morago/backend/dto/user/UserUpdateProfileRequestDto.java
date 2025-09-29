package com.morago.backend.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateProfileRequestDto {
    @Schema(example = "Eva")
    @Size(max = 50)
    private String firstName;

    @Schema(example = "Lee")
    @Size(max = 50)
    private String lastName;
}
