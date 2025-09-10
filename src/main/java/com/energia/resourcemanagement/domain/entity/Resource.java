package com.energia.resourcemanagement.domain.entity;

import com.energia.resourcemanagement.domain.enums.ResourceType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "characteristics")
@ToString(exclude = "characteristics")
public class Resource {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 50)
    private ResourceType type;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "country_code", nullable = false, updatable = false, length = 2)
    private String countryCode;

    @Valid
    @Embedded
    private Location location;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Characteristic> characteristics = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Helper methods
    public void addCharacteristic(Characteristic characteristic) {
        characteristics.add(characteristic);
        characteristic.setResource(this);
    }

    public void removeCharacteristic(Characteristic characteristic) {
        characteristics.remove(characteristic);
        characteristic.setResource(null);
    }

    public void updateCharacteristics(List<Characteristic> newCharacteristics) {

        this.characteristics.clear();


        if (newCharacteristics != null) {
            newCharacteristics.forEach(this::addCharacteristic);
        }
    }
}