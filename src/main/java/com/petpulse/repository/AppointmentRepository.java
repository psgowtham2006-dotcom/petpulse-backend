package com.petpulse.repository;

import com.petpulse.entity.Appointment;
import com.petpulse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByVeterinarian(User veterinarian);
    List<Appointment> findByPetOwnerId(Long ownerId);
}
