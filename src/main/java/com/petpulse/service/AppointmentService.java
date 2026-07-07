package com.petpulse.service;

import com.petpulse.entity.Appointment;
import com.petpulse.entity.Pet;
import com.petpulse.entity.User;
import com.petpulse.exception.BusinessValidationException;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.repository.AppointmentRepository;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PetRepository petRepository,
                                  UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment scheduleAppointment(Appointment appointment) {
        if (appointment.getPet() == null || appointment.getPet().getId() == null) {
            throw new IllegalArgumentException("Pet id is required");
        }
        if (appointment.getVeterinarian() == null || appointment.getVeterinarian().getId() == null) {
            throw new IllegalArgumentException("Veterinarian id is required");
        }
        Pet pet = petRepository.findById(appointment.getPet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: "
                        + appointment.getPet().getId()));
        User veterinarian = userRepository.findById(appointment.getVeterinarian().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with id: "
                        + appointment.getVeterinarian().getId()));
        if (veterinarian.getRole() != User.Role.ROLE_VET) {
            throw new BusinessValidationException("Error: Selected user is not a veterinarian!");
        }
        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        boolean overlap = appointmentRepository.findByVeterinarian(appointment.getVeterinarian()).stream()
                .filter(existing -> !"CANCELLED".equalsIgnoreCase(existing.getStatus()))
                .anyMatch(existing -> Math.abs(Duration.between(existing.getAppointmentDate(),
                        appointment.getAppointmentDate()).toMinutes()) < 30);
        if (overlap) {
            throw new BusinessValidationException("Error: Veterinarian has an overlapping appointment!");
        }
        appointment.setStatus("SCHEDULED");
        return appointmentRepository.save(appointment);
    }

    public Appointment updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }
}
