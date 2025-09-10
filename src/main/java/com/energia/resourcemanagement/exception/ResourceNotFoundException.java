package com.energia.resourcemanagement.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(UUID id) {
        super(String.format("Resource with id %s not found", id));
    }
}



