package com.petpulse.service;

import com.petpulse.entity.AdoptionRequest;
import com.petpulse.entity.Pet;
import com.petpulse.entity.Shelter;
import com.petpulse.entity.User;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.repository.AdoptionRequestRepository;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.ShelterRepository;
import com.petpulse.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdoptionService {

    private static final Logger log = LoggerFactory.getLogger(AdoptionService.class);

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public AdoptionService(AdoptionRequestRepository adoptionRequestRepository, ShelterRepository shelterRepository,
                               PetRepository petRepository, UserRepository userRepository) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AdoptionRequest> getAllRequests() {
        return adoptionRequestRepository.findAll();
    }

    public AdoptionRequest createRequest(AdoptionRequest request) {
        if (request.getApplicant() == null || request.getApplicant().getId() == null) {
            throw new IllegalArgumentException("Applicant id is required");
        }
        if (request.getPet() == null || request.getPet().getId() == null) {
            throw new IllegalArgumentException("Pet id is required");
        }
        User applicant = userRepository.findById(request.getApplicant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with id: "
                        + request.getApplicant().getId()));
        Pet pet = petRepository.findById(request.getPet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + request.getPet().getId()));
        if (!pet.isAvailableForAdoption()) {
            throw new IllegalArgumentException("Pet is not available for adoption");
        }
        request.setApplicant(applicant);
        request.setPet(pet);
        request.setStatus("PENDING");
        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDate.now());
        }
        return adoptionRequestRepository.save(request);
    }

    public AdoptionRequest updateRequestStatus(Long id, String status) {
        AdoptionRequest request = adoptionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found with id: " + id));
        request.setStatus(status);
        if ("APPROVED".equalsIgnoreCase(status)) {
            Pet pet = request.getPet();
            pet.setAvailableForAdoption(false);
            pet.setOwner(request.getApplicant());
            petRepository.save(pet);
            log.info("Adoption request {} approved; pet {} transferred to user {}", id, pet.getId(),
                    request.getApplicant().getId());
        }
        return adoptionRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean canShelterAcceptPet(Long shelterId) {
        if (shelterId == null) {
            return true;
        }
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found with id: " + shelterId));
        long assignedPets = petRepository.findAll().stream()
                .filter(pet -> pet.getShelter() != null && shelterId.equals(pet.getShelter().getId()))
                .count();
        return assignedPets < shelter.getCapacity();
    }
}
