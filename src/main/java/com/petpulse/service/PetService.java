package com.petpulse.service;

import com.petpulse.entity.Pet;
import com.petpulse.entity.Shelter;
import com.petpulse.entity.User;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.ShelterRepository;
import com.petpulse.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository, ShelterRepository shelterRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.shelterRepository = shelterRepository;
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pet> getPetsByOwner(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + username));
        return petRepository.findByOwner(owner);
    }

    @Transactional(readOnly = true)
    public List<Pet> getPetsForAdoption() {
        return petRepository.findByAvailableForAdoptionTrue();
    }

    public Pet savePet(Pet pet) {
        if (pet.getOwner() != null && pet.getOwner().getId() != null) {
            User owner = userRepository.findById(pet.getOwner().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: "
                            + pet.getOwner().getId()));
            pet.setOwner(owner);
        }
        if (pet.getShelter() != null && pet.getShelter().getId() != null) {
            Shelter shelter = shelterRepository.findById(pet.getShelter().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shelter not found with id: "
                            + pet.getShelter().getId()));
            pet.setShelter(shelter);
        }
        log.info("Saving pet {}", pet.getPetName());
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
