package com.petpulse.repository;

import com.petpulse.entity.Pet;
import com.petpulse.entity.Vaccination;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByPet(Pet pet);
}
