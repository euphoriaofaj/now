package com.morago.backend.dto.translator;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorRegistrationRequestDto {

    @NotBlank(message = "Phone number is required")
    @JsonAlias({"phone_number", "phone", "phoneNo"})
    @Schema(
            example = "01012345678",
            pattern = "^010\\d{8}$"
    )
    @Pattern(
            regexp = "^010\\d{8}$")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 9, max = 72, message = "Password must be 9-72 chars")
    @Schema(example = "P@ssw0rd!", minLength = 8, maxLength = 72)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(example = "P@ssw0rd!")
    private String confirmPassword;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @AssertTrue(message = "Passwords do not match")
    @JsonIgnore
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
