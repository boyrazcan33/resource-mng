package com.energia.resourcemanagement.mapper;

import com.energia.resourcemanagement.domain.entity.Characteristic;
import com.energia.resourcemanagement.domain.entity.Location;
import com.energia.resourcemanagement.domain.entity.Resource;
import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import com.energia.resourcemanagement.dto.request.CreateResourceRequest;
import com.energia.resourcemanagement.dto.response.ResourceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    // Entity to DTO
    ResourceResponse toResponse(Resource resource);

    List<ResourceResponse> toResponseList(List<Resource> resources);

    LocationDTO toLocationDTO(Location location);

    CharacteristicDTO toCharacteristicDTO(Characteristic characteristic);

    List<CharacteristicDTO> toCharacteristicDTOList(List<Characteristic> characteristics);

    // DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "characteristics", ignore = true)
    Resource toEntity(CreateResourceRequest request);

    Location toLocation(LocationDTO locationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resource", ignore = true)
    Characteristic toCharacteristic(CharacteristicDTO characteristicDTO);

    List<Characteristic> toCharacteristicList(List<CharacteristicDTO> characteristicDTOs);

    // Update Entity from DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "countryCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "characteristics", ignore = true)
    void updateResourceFromDTO(LocationDTO locationDTO, @MappingTarget Resource resource);
}