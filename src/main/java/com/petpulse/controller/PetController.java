package com.petpulse.controller;

import com.petpulse.entity.Pet;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.service.AdoptionService;
import com.petpulse.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet registration and adoption availability")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

    private static final String CRUD_CREATE_MSG = "Pet created successfully.";
    private static final String CRUD_UPDATE_MSG = "Pet updated successfully.";
    private static final String CRUD_DELETE_MSG = "Pet deleted successfully.";
    private static final String CAPACITY_MSG = "Error: Shelter is at maximum capacity!";

    private final PetService petService;
    private final AdoptionService adoptionService;

    public PetController(PetService petService, AdoptionService adoptionService) {
        this.petService = petService;
        this.adoptionService = adoptionService;
    }

    @GetMapping
    @Operation(summary = "List pets", description = "Returns all registered pets")
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet", description = "Returns a pet by id")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('SHELTER') or hasRole('ADMIN')")
    @Operation(summary = "Create pet", description = "Creates a pet after shelter capacity validation")
    public ResponseEntity<String> createPet(@Valid @RequestBody Pet pet) {
        if (pet.getShelter() != null && !adoptionService.canShelterAcceptPet(pet.getShelter().getId())) {
            return ResponseEntity.badRequest().body(CAPACITY_MSG);
        }
        petService.savePet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(CRUD_CREATE_MSG);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('SHELTER') or hasRole('ADMIN')")
    @Operation(summary = "Update pet", description = "Updates pet details after shelter capacity validation")
    public ResponseEntity<String> updatePet(@PathVariable Long id, @Valid @RequestBody Pet petDetails) {
        Pet pet = petService.getPetById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (petDetails.getShelter() != null
                && (pet.getShelter() == null || !petDetails.getShelter().getId().equals(pet.getShelter().getId()))
                && !adoptionService.canShelterAcceptPet(petDetails.getShelter().getId())) {
            return ResponseEntity.badRequest().body(CAPACITY_MSG);
        }
        pet.setPetName(petDetails.getPetName());
        pet.setSpecies(petDetails.getSpecies());
        pet.setBreed(petDetails.getBreed());
        pet.setAge(petDetails.getAge());
        pet.setGender(petDetails.getGender());
        pet.setImageUrl(petDetails.getImageUrl());
        pet.setOwner(petDetails.getOwner());
        pet.setShelter(petDetails.getShelter());
        pet.setAvailableForAdoption(petDetails.isAvailableForAdoption());
        petService.savePet(pet);
        return ResponseEntity.ok(CRUD_UPDATE_MSG);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Delete pet", description = "Deletes a pet by id")
    public ResponseEntity<String> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok(CRUD_DELETE_MSG);
    }

    @GetMapping("/adoption")
    @Operation(summary = "Adoption pets", description = "Returns pets currently available for adoption")
    public ResponseEntity<List<Pet>> getPetsForAdoption() {
        return ResponseEntity.ok(petService.getPetsForAdoption());
    }
}
