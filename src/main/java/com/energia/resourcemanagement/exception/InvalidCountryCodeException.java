package com.energia.resourcemanagement.exception;

import java.util.UUID;

public class InvalidCountryCodeException extends RuntimeException {
    public InvalidCountryCodeException(String countryCode) {
        super(String.format("Invalid country code: %s. Must be 2 uppercase letters (ISO 3166-1 alpha-2)", countryCode));
    }
}