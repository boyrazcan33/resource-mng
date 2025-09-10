package com.energia.resourcemanagement.dto.request;

import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResourceRequest {

    @Valid
    private LocationDTO location;

    @Valid
    private List<CharacteristicDTO> characteristics;
}