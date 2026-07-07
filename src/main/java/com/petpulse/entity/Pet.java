package com.petpulse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pets", indexes = {
        @Index(name = "idx_pets_owner_id", columnList = "owner_id"),
        @Index(name = "idx_pets_shelter_id", columnList = "shelter_id"),
        @Index(name = "idx_pets_available", columnList = "available_for_adoption")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Column(nullable = false)
    private String petName;

    @NotBlank(message = "Species is required")
    @Column(nullable = false)
    private String species;

    private String breed;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Column(nullable = false)
    private Integer age;

    private String gender;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    @Column(name = "available_for_adoption", nullable = false)
    private boolean availableForAdoption;
}
