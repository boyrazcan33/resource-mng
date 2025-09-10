package com.energia.resourcemanagement.config;

import com.energia.resourcemanagement.domain.entity.Characteristic;
import com.energia.resourcemanagement.domain.entity.Location;
import com.energia.resourcemanagement.domain.entity.Resource;
import com.energia.resourcemanagement.domain.enums.CharacteristicType;
import com.energia.resourcemanagement.domain.enums.ResourceType;
import com.energia.resourcemanagement.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @Profile("!test")
    CommandLineRunner initDatabase(ResourceRepository resourceRepository) {
        return args -> {
            if (resourceRepository.count() == 0) {
                log.info("Initializing database with sample data...");

                // Resource 1: Metering Point in Estonia
                Resource resource1 = Resource.builder()
                        .type(ResourceType.METERING_POINT)
                        .countryCode("EE")
                        .location(Location.builder()
                                .streetAddress("Viru 1")
                                .city("Tallinn")
                                .postalCode("10111")
                                .countryCode("EE")
                                .build())
                        .build();

                Characteristic char1_1 = Characteristic.builder()
                        .code("CONS1")
                        .type(CharacteristicType.CONSUMPTION_TYPE)
                        .value("RESIDENTIAL")
                        .build();

                Characteristic char1_2 = Characteristic.builder()
                        .code("CP001")
                        .type(CharacteristicType.CHARGING_POINT)
                        .value("TYPE_2")
                        .build();

                resource1.addCharacteristic(char1_1);
                resource1.addCharacteristic(char1_2);

                // Resource 2: Connection Point in Finland
                Resource resource2 = Resource.builder()
                        .type(ResourceType.CONNECTION_POINT)
                        .countryCode("FI")
                        .location(Location.builder()
                                .streetAddress("Mannerheimintie 1")
                                .city("Helsinki")
                                .postalCode("00100")
                                .countryCode("FI")
                                .build())
                        .build();

                Characteristic char2_1 = Characteristic.builder()
                        .code("STAT1")
                        .type(CharacteristicType.CONNECTION_POINT_STATUS)
                        .value("ACTIVE")
                        .build();

                Characteristic char2_2 = Characteristic.builder()
                        .code("CONS2")
                        .type(CharacteristicType.CONSUMPTION_TYPE)
                        .value("COMMERCIAL")
                        .build();

                resource2.addCharacteristic(char2_1);
                resource2.addCharacteristic(char2_2);

                // Resource 3: Metering Point in Finland
                Resource resource3 = Resource.builder()
                        .type(ResourceType.METERING_POINT)
                        .countryCode("FI")
                        .location(Location.builder()
                                .streetAddress("Aleksanterinkatu 52")
                                .city("Helsinki")
                                .postalCode("00100")
                                .countryCode("FI")
                                .build())
                        .build();

                Characteristic char3_1 = Characteristic.builder()
                        .code("CONS3")
                        .type(CharacteristicType.CONSUMPTION_TYPE)
                        .value("INDUSTRIAL")
                        .build();

                Characteristic char3_2 = Characteristic.builder()
                        .code("CP002")
                        .type(CharacteristicType.CHARGING_POINT)
                        .value("CCS")
                        .build();

                Characteristic char3_3 = Characteristic.builder()
                        .code("STAT2")
                        .type(CharacteristicType.CONNECTION_POINT_STATUS)
                        .value("MAINTENANCE")
                        .build();

                resource3.addCharacteristic(char3_1);
                resource3.addCharacteristic(char3_2);
                resource3.addCharacteristic(char3_3);

                // Resource 4: Connection Point in Estonia
                Resource resource4 = Resource.builder()
                        .type(ResourceType.CONNECTION_POINT)
                        .countryCode("EE")
                        .location(Location.builder()
                                .streetAddress("Narva mnt 5")
                                .city("Tallinn")
                                .postalCode("10117")
                                .countryCode("EE")
                                .build())
                        .build();

                Characteristic char4_1 = Characteristic.builder()
                        .code("STAT3")
                        .type(CharacteristicType.CONNECTION_POINT_STATUS)
                        .value("INACTIVE")
                        .build();

                resource4.addCharacteristic(char4_1);

                resourceRepository.saveAll(Arrays.asList(resource1, resource2, resource3, resource4));

                log.info("Sample data initialization completed. Created {} resources.", 4);
            } else {
                log.info("Database already contains data. Skipping initialization.");
            }
        };
    }
}