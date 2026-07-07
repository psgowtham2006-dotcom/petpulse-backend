package com.petpulse.service;

import com.petpulse.entity.MedicalRecord;
import com.petpulse.entity.Pet;
import com.petpulse.entity.User;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.repository.MedicalRecordRepository;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PetRepository petRepository,
                                    UserRepository userRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<MedicalRecord> getAllRecords() {
        return medicalRecordRepository.findAll();
    }

    public MedicalRecord saveRecord(MedicalRecord record) {
        if (record.getPet() == null || record.getPet().getId() == null) {
            throw new IllegalArgumentException("Pet id is required");
        }
        if (record.getVeterinarian() == null || record.getVeterinarian().getId() == null) {
            throw new IllegalArgumentException("Veterinarian id is required");
        }
        Pet pet = petRepository.findById(record.getPet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: "
                        + record.getPet().getId()));
        User veterinarian = userRepository.findById(record.getVeterinarian().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with id: "
                        + record.getVeterinarian().getId()));
        record.setPet(pet);
        record.setVeterinarian(veterinarian);
        if (record.getStatus() == null || record.getStatus().isBlank()) {
            record.setStatus("PENDING");
        }
        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getRecordsByPetId(Long petId) {
        return medicalRecordRepository.findAll().stream()
                .filter(record -> record.getPet() != null && record.getPet().getId().equals(petId))
                .toList();
    }

    public MedicalRecord approveRecord(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
        record.setStatus("APPROVED");
        return medicalRecordRepository.save(record);
    }
}
