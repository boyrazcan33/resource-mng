package com.energia.resourcemanagement.dto.request;

import com.energia.resourcemanagement.domain.enums.ResourceType;
import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest {

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    @NotNull(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters (ISO 3166-1 alpha-2)")
    private String countryCode;

    @NotNull(message = "Location is required")
    @Valid
    private LocationDTO location;

    @Valid
    private List<CharacteristicDTO> characteristics;
}