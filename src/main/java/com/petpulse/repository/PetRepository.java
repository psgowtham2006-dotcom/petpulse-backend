package com.petpulse.repository;

import com.petpulse.entity.Pet;
import com.petpulse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner(User owner);
    List<Pet> findByAvailableForAdoptionTrue();
}
