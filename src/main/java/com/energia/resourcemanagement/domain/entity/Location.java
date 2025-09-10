package com.energia.resourcemanagement.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @NotBlank
    @Size(max = 255)
    @Column(name = "street_address")
    private String streetAddress;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city")
    private String city;

    @NotBlank
    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "location_country_code")
    private String countryCode;
}