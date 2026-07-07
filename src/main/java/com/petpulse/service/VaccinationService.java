package com.petpulse.service;

import com.petpulse.entity.Pet;
import com.petpulse.entity.Vaccination;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.VaccinationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;

    public VaccinationService(VaccinationRepository vaccinationRepository, PetRepository petRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    public List<Vaccination> getAllVaccinations() {
        return vaccinationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vaccination> getVaccinationsByPet(Pet pet) {
        return vaccinationRepository.findByPet(pet);
    }

    public Vaccination saveVaccination(Vaccination vaccination) {
        if (vaccination.getPet() == null || vaccination.getPet().getId() == null) {
            throw new IllegalArgumentException("Pet id is required");
        }
        Pet pet = petRepository.findById(vaccination.getPet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: "
                        + vaccination.getPet().getId()));
        vaccination.setPet(pet);
        return vaccinationRepository.save(vaccination);
    }

    public void deleteVaccination(Long id) {
        if (!vaccinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vaccination not found with id: " + id);
        }
        vaccinationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Vaccination> getUpcomingReminders() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(7);
        return vaccinationRepository.findAll().stream()
                .filter(vaccination -> vaccination.getNextDueDate() != null)
                .filter(vaccination -> !vaccination.getNextDueDate().isBefore(start)
                        && !vaccination.getNextDueDate().isAfter(end))
                .toList();
    }
}
