package com.energia.resourcemanagement.dto.response;

import com.energia.resourcemanagement.domain.enums.ResourceType;
import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private UUID id;
    private ResourceType type;
    private String countryCode;
    private LocationDTO location;
    private List<CharacteristicDTO> characteristics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}