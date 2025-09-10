package com.energia.resourcemanagement.dto.common;

import com.energia.resourcemanagement.domain.enums.CharacteristicType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicDTO {

    @NotBlank(message = "Code is required")
    @Size(min = 1, max = 5, message = "Code must be between 1 and 5 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Code must contain only uppercase letters and numbers")
    private String code;

    @NotNull(message = "Type is required")
    private CharacteristicType type;

    @NotBlank(message = "Value is required")
    @Size(max = 255, message = "Value must not exceed 255 characters")
    private String value;
}