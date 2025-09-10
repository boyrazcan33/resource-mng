package com.energia.resourcemanagement.integration.controller;

import com.energia.resourcemanagement.domain.enums.CharacteristicType;
import com.energia.resourcemanagement.domain.enums.ResourceType;
import com.energia.resourcemanagement.dto.common.CharacteristicDTO;
import com.energia.resourcemanagement.dto.common.LocationDTO;
import com.energia.resourcemanagement.dto.request.CreateResourceRequest;
import com.energia.resourcemanagement.dto.request.UpdateResourceRequest;
import com.energia.resourcemanagement.integration.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ResourceControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaConsumer<String, String> kafkaConsumer;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList("resource-events"));
    }

    @AfterEach
    void tearDown() {
        if (kafkaConsumer != null) {
            kafkaConsumer.close();
        }
    }

    @Test
    void createResource_Success() throws Exception {
        CreateResourceRequest request = CreateResourceRequest.builder()
                .type(ResourceType.METERING_POINT)
                .countryCode("EE")
                .location(LocationDTO.builder()
                        .streetAddress("Integration Test Street")
                        .city("Tallinn")
                        .postalCode("12345")
                        .countryCode("EE")
                        .build())
                .characteristics(List.of(
                        CharacteristicDTO.builder()
                                .code("INT01")
                                .type(CharacteristicType.CONSUMPTION_TYPE)
                                .value("RESIDENTIAL")
                                .build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("METERING_POINT"))
                .andExpect(jsonPath("$.countryCode").value("EE"))
                .andExpect(jsonPath("$.location.city").value("Tallinn"))
                .andExpect(jsonPath("$.characteristics[0].code").value("INT01"))
                .andReturn();

        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThan(0);
        records.forEach(record -> {
            assertThat(record.value()).contains("RESOURCE_CREATED");
        });
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
        mockMvc.perform(get("/api/v1/resources")
                        .param("countryCode", "EE")
                        .param("type", "METERING_POINT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
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
                .build();

        mockMvc.perform(put("/api/v1/resources/{id}", resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.streetAddress").value("Updated Street"))
                .andExpect(jsonPath("$.location.city").value("Tartu"));

        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThan(0);
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

        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThan(0);
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
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
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
        mockMvc.perform(post("/api/v1/resources/export-all"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Export initiated successfully"))
                .andExpect(jsonPath("$.totalResources").isNumber())
                .andExpect(jsonPath("$.jobId").isNotEmpty());
    }
}