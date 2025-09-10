package com.energia.resourcemanagement.exception;

import java.util.UUID;

public class DuplicateCharacteristicException extends RuntimeException {
    public DuplicateCharacteristicException(String code, String type) {
        super(String.format("Characteristic with code '%s' and type '%s' already exists for this resource", code, type));
    }
}