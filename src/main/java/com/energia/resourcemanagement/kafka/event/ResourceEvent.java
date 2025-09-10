package com.energia.resourcemanagement.kafka.event;

import com.energia.resourcemanagement.domain.enums.EventType;
import com.energia.resourcemanagement.dto.response.ResourceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEvent {

    private UUID eventId;
    private EventType eventType;
    private UUID resourceId;
    private ResourceResponse resource;
    private Instant timestamp;
}