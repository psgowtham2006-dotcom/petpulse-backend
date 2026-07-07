package com.petpulse.repository;

import com.petpulse.entity.MedicalRecord;
import com.petpulse.entity.Pet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPet(Pet pet);
}
