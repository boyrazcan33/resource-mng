package com.energia.resourcemanagement.integration.controller;

import com.energia.resourcemanagement.domain.enums.CharacteristicType;
import com.energia.resourcemanagement.domain.enums.ResourceType;
import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import com.energia.resourcemanagement.dto.request.CreateResourceRequest;
import com.energia.resourcemanagement.dto.request.UpdateResourceRequest;
import com.energia.resourcemanagement.integration.AbstractIntegrationTest;
import com.energia.resourcemanagement.repository.ResourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class ResourceControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
    }

    @Test
    void createResource_Success() throws Exception {
        CreateResourceRequest request = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Test Street 1")
                        .city("Tallinn")
                        .postalCode("12345")
                        .countryCode("EE")
                        .build())
                .characteristics(List.of(
                        CharacteristicDTO.builder()
                                .code("TEST1")
                                .type(CharacteristicType.CONSUMPTION_TYPE)
                                .value("RESIDENTIAL")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("METERING_POINT"))
                .andExpect(jsonPath("$.countryCode").value("EE"))
                .andExpect(jsonPath("$.location.city").value("Tallinn"))
                .andExpect(jsonPath("$.characteristics[0].code").value("TEST1"));
    }

    @Test
    void getResource_Success() throws Exception {
        CreateResourceRequest createRequest = CreateResourceRequest.builder()
                .type(ResourceType.CONNECTION_POINT)
                .countryCode("FI")
                .location(LocationDTO.builder()
                        .streetAddress("Test Street")
                        .city("Helsinki")
                        .postalCode("00100")
                        .countryCode("FI")
                        .build())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String resourceId = objectMapper.readTree(responseBody).get("id").asText();

        mockMvc.perform(get("/api/v1/resources/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId))
                .andExpect(jsonPath("$.type").value("CONNECTION_POINT"))
                .andExpect(jsonPath("$.countryCode").value("FI"));
    }

    @Test
    void getResource_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/resources/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void getAllResources_WithFilters() throws Exception {
        CreateResourceRequest request1 = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Filter Test 1")
                        .city("Tallinn")
                        .postalCode("11111")
                        .countryCode("EE")
                        .build())
                .build();

        CreateResourceRequest request2 = CreateResourceRequest.builder()
                .type(ResourceType.CONNECTION_POINT)
                .countryCode("FI")
                .location(LocationDTO.builder()
                        .streetAddress("Filter Test 2")
                        .city("Helsinki")
                        .postalCode("22222")
                        .countryCode("FI")
                        .build())
                .build();

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/resources")
                        .param("countryCode", "EE")
                        .param("type", "METERING_POINT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].countryCode").value("EE"))
                .andExpect(jsonPath("$.content[0].type").value("METERING_POINT"));
    }

    @Test
    void updateResource_Success() throws Exception {
        CreateResourceRequest createRequest = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Original Street")
                        .city("Tallinn")
                        .postalCode("11111")
                        .countryCode("EE")
                        .build())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String resourceId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        UpdateResourceRequest updateRequest = UpdateResourceRequest.builder()
                .location(LocationDTO.builder()
                        .streetAddress("Updated Street")
                        .city("Tartu")
                        .postalCode("22222")
                        .countryCode("EE")
                        .build())
                .characteristics(List.of(
                        CharacteristicDTO.builder()
                                .code("UPD01")
                                .type(CharacteristicType.CONSUMPTION_TYPE)
                                .value("COMMERCIAL")
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/resources/{id}", resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.streetAddress").value("Updated Street"))
                .andExpect(jsonPath("$.location.city").value("Tartu"))
                .andExpect(jsonPath("$.characteristics[0].code").value("UPD01"));
    }

    @Test
    void deleteResource_Success() throws Exception {
        CreateResourceRequest createRequest = CreateResourceRequest.builder()
                .type(ResourceType.CONNECTION_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Delete Test")
                        .city("Tallinn")
                        .postalCode("33333")
                        .countryCode("EE")
                        .build())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String resourceId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(delete("/api/v1/resources/{id}", resourceId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/resources/{id}", resourceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createResource_ValidationError() throws Exception {
        CreateResourceRequest invalidRequest = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("INVALID")
                .location(LocationDTO.builder()
                        .streetAddress("")
                        .city("Tallinn")
                        .postalCode("12345")
                        .countryCode("EE")
                        .build())
                .build();

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createResource_DuplicateCharacteristic() throws Exception {
        CreateResourceRequest request = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Test Street")
                        .city("Tallinn")
                        .postalCode("12345")
                        .countryCode("EE")
                        .build())
                .characteristics(List.of(
                        CharacteristicDTO.builder()
                                .code("DUP01")
                                .type(CharacteristicType.CONSUMPTION_TYPE)
                                .value("VALUE1")
                                .build(),
                        CharacteristicDTO.builder()
                                .code("DUP01")
                                .type(CharacteristicType.CONSUMPTION_TYPE)
                                .value("VALUE2")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CHARACTERISTIC"));
    }

    @Test
    void exportAllResources_Success() throws Exception {
        CreateResourceRequest request = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Export Test")
                        .city("Tallinn")
                        .postalCode("44444")
                        .countryCode("EE")
                        .build())
                .build();

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/resources/export-all"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Export initiated successfully"))
                .andExpect(jsonPath("$.totalResources").isNumber())
                .andExpect(jsonPath("$.jobId").isNotEmpty());
    }

    @Test
    void updateResource_OptimisticLocking() throws Exception {
        CreateResourceRequest createRequest = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Version Test")
                        .city("Tallinn")
                        .postalCode("55555")
                        .countryCode("EE")
                        .build())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String resourceId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asText();

        UpdateResourceRequest updateRequest = UpdateResourceRequest.builder()
                .location(LocationDTO.builder()
                        .streetAddress("Version Update")
                        .city("Tallinn")
                        .postalCode("55555")
                        .countryCode("EE")
                        .build())
                .build();

        mockMvc.perform(put("/api/v1/resources/{id}", resourceId)
                        .header("If-Match", "999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONCURRENT_UPDATE"));
    }
}