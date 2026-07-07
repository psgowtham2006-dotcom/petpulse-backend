package com.petpulse.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.petpulse.entity.User;
import com.petpulse.repository.AdoptionRequestRepository;
import com.petpulse.repository.AppointmentRepository;
import com.petpulse.repository.MedicalRecordRepository;
import com.petpulse.repository.PetRepository;
import com.petpulse.repository.ShelterRepository;
import com.petpulse.repository.UserRepository;
import com.petpulse.repository.VaccinationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class DataInitializerTest {

    @Test
    void runShouldCreateGowthamAdminUserForLogin() {
        UserRepository userRepository = mock(UserRepository.class);
        ShelterRepository shelterRepository = mock(ShelterRepository.class);
        PetRepository petRepository = mock(PetRepository.class);
        VaccinationRepository vaccinationRepository = mock(VaccinationRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        MedicalRecordRepository medicalRecordRepository = mock(MedicalRecordRepository.class);
        AdoptionRequestRepository adoptionRequestRepository = mock(AdoptionRequestRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.empty());
        when(userRepository.findByUsername("gowtham")).thenReturn(java.util.Optional.empty());
        when(userRepository.findByUsername("vet")).thenReturn(java.util.Optional.empty());
        when(userRepository.findByUsername("owner")).thenReturn(java.util.Optional.empty());
        when(userRepository.findByUsername("shelter")).thenReturn(java.util.Optional.empty());
        when(shelterRepository.count()).thenReturn(0L);
        when(petRepository.count()).thenReturn(0L);
        when(vaccinationRepository.count()).thenReturn(0L);
        when(appointmentRepository.count()).thenReturn(0L);
        when(medicalRecordRepository.count()).thenReturn(0L);
        when(adoptionRequestRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");

        DataInitializer initializer = new DataInitializer(
                userRepository,
                shelterRepository,
                petRepository,
                vaccinationRepository,
                appointmentRepository,
                medicalRecordRepository,
                adoptionRequestRepository,
                passwordEncoder);

        initializer.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, org.mockito.Mockito.atLeast(1)).save(userCaptor.capture());
        assertEquals("gowtham", userCaptor.getAllValues().stream()
                .filter(user -> "gowtham".equals(user.getUsername()))
                .findFirst()
                .orElseThrow()
                .getUsername());
    }
}
