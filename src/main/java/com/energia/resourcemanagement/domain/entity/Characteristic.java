package com.energia.resourcemanagement.domain.entity;

import com.energia.resourcemanagement.domain.enums.CharacteristicType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "characteristics",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"resource_id", "code", "type"},
                name = "uk_resource_code_type"
        ))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "resource")
@ToString(exclude = "resource")
public class Characteristic {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 5)
    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CharacteristicType type;

    @NotBlank
    @Size(max = 255)
    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}