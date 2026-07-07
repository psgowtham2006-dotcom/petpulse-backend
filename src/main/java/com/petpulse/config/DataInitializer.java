package com.petpulse.config;

import com.petpulse.entity.AdoptionRequest;
import com.petpulse.entity.Appointment;
import com.petpulse.entity.MedicalRecord;
import com.petpulse.entity.Pet;
import com.petpulse.entity.Shelter;
import com.petpulse.entity.User;
import com.petpulse.entity.Vaccination;
import com.petpulse.repository.AdoptionRequestRepository;
import com.petpulse.repository.AppointmentRepository;
import com.petpulse.repository.MedicalRecordRepository;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.ShelterRepository;
import com.petpulse.repository.UserRepository;
import com.petpulse.repository.VaccinationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final VaccinationRepository vaccinationRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ShelterRepository shelterRepository,
                           PetRepository petRepository, VaccinationRepository vaccinationRepository,
                           AppointmentRepository appointmentRepository,
                           MedicalRecordRepository medicalRecordRepository,
                           AdoptionRequestRepository adoptionRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> user("admin", User.Role.ROLE_ADMIN, "Admin User", "admin@petpulse.com", "9000000001"));
        User gowtham = userRepository.findByUsername("gowtham")
                .orElseGet(() -> user("gowtham", User.Role.ROLE_ADMIN, "Gowtham", "gowtham@petpulse.com", "9000000005"));
        User vet = userRepository.findByUsername("vet")
                .orElseGet(() -> user("vet", User.Role.ROLE_VET, "Dr. Vet", "vet@petpulse.com", "9000000002"));
        User owner = userRepository.findByUsername("owner")
                .orElseGet(() -> user("owner", User.Role.ROLE_OWNER, "Pet Owner", "owner@petpulse.com", "9000000003"));
        User shelterUser = userRepository.findByUsername("shelter")
                .orElseGet(() -> user("shelter", User.Role.ROLE_SHELTER, "Shelter Manager", "shelter@petpulse.com", "9000000004"));

        userRepository.save(admin);
        userRepository.save(gowtham);
        userRepository.save(vet);
        userRepository.save(owner);
        userRepository.save(shelterUser);

        if (shelterRepository.count() == 0) {
            Shelter shelter = new Shelter(null, "PetPulse City Shelter", "Bengaluru", 50);
            shelterRepository.save(shelter);
        }
        Shelter shelter = shelterRepository.findAll().stream().findFirst().orElse(null);
        if (shelter == null) {
            shelter = new Shelter(null, "PetPulse City Shelter", "Bengaluru", 50);
            shelterRepository.save(shelter);
        }

        if (petRepository.count() == 0) {
            Pet buddy = new Pet(null, "Buddy", "Dog", "Labrador", 3, "Male", null, owner, null, false);
            Pet luna = new Pet(null, "Luna", "Cat", "Domestic Short Hair", 2, "Female", null, null, shelter, true);
            petRepository.save(buddy);
            petRepository.save(luna);
        }

        if (vaccinationRepository.count() == 0) {
            Pet buddy = petRepository.findAll().stream().filter(pet -> "Buddy".equals(pet.getPetName())).findFirst().orElse(null);
            Pet luna = petRepository.findAll().stream().filter(pet -> "Luna".equals(pet.getPetName())).findFirst().orElse(null);
            if (buddy != null) {
                vaccinationRepository.save(new Vaccination(null, "Rabies", LocalDate.now().minusMonths(10),
                        LocalDate.now().plusDays(5), "PENDING", buddy));
            }
            if (luna != null) {
                vaccinationRepository.save(new Vaccination(null, "FVRCP", LocalDate.now().minusMonths(6),
                        LocalDate.now().plusMonths(6), "COMPLETED", luna));
            }
        }

        if (appointmentRepository.count() == 0) {
            Pet buddy = petRepository.findAll().stream().filter(pet -> "Buddy".equals(pet.getPetName())).findFirst().orElse(null);
            if (buddy != null) {
                appointmentRepository.save(new Appointment(null, LocalDateTime.now().plusDays(2).withSecond(0).withNano(0),
                        "Wellness check", "SCHEDULED", buddy, vet));
            }
        }

        if (medicalRecordRepository.count() == 0) {
            Pet buddy = petRepository.findAll().stream().filter(pet -> "Buddy".equals(pet.getPetName())).findFirst().orElse(null);
            if (buddy != null) {
                medicalRecordRepository.save(new MedicalRecord(null, "Healthy", "Routine care", LocalDate.now(),
                        "APPROVED", vet, buddy));
            }
        }

        if (adoptionRequestRepository.count() == 0) {
            Pet luna = petRepository.findAll().stream().filter(pet -> "Luna".equals(pet.getPetName())).findFirst().orElse(null);
            if (luna != null) {
                adoptionRequestRepository.save(new AdoptionRequest(null, LocalDate.now(), "PENDING", owner, luna));
            }
        }

        log.info("Seeded default PetPulse data. Default password for all users: {}", DEFAULT_PASSWORD);
    }

    private User user(String username, User.Role role, String fullName, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRole(role);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }
}
