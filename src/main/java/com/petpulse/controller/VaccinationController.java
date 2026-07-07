package com.petpulse.controller;

import com.petpulse.entity.Vaccination;
import com.petpulse.exception.ResourceNotFoundException;
import com.petpulse.service.VaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vaccinations")
@Tag(name = "Vaccinations", description = "Vaccination tracking and reminders")
@SecurityRequirement(name = "bearerAuth")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @GetMapping
    @Operation(summary = "List vaccinations", description = "Returns all vaccination records")
    public ResponseEntity<List<Vaccination>> getAllVaccinations() {
        return ResponseEntity.ok(vaccinationService.getAllVaccinations());
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('VET') or hasRole('ADMIN')")
    @Operation(summary = "Create vaccination", description = "Records or schedules a vaccination")
    public ResponseEntity<Vaccination> createVaccination(@Valid @RequestBody Vaccination vaccination) {
        return ResponseEntity.ok(vaccinationService.saveVaccination(vaccination));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VET') or hasRole('ADMIN')")
    @Operation(summary = "Update vaccination", description = "Updates vaccination details")
    public ResponseEntity<Vaccination> updateVaccination(@PathVariable Long id,
                                                         @Valid @RequestBody Vaccination details) {
        Vaccination vaccination = vaccinationService.getAllVaccinations().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination not found with id: " + id));
        vaccination.setVaccineName(details.getVaccineName());
        vaccination.setVaccinationDate(details.getVaccinationDate());
        vaccination.setNextDueDate(details.getNextDueDate());
        vaccination.setStatus(details.getStatus());
        vaccination.setPet(details.getPet());
        return ResponseEntity.ok(vaccinationService.saveVaccination(vaccination));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete vaccination", description = "Deletes a vaccination by id")
    public ResponseEntity<String> deleteVaccination(@PathVariable Long id) {
        vaccinationService.deleteVaccination(id);
        return ResponseEntity.ok("Vaccination deleted successfully.");
    }

    @GetMapping("/reminders")
    @Operation(summary = "Vaccination reminders", description = "Returns vaccinations due within seven days")
    public ResponseEntity<List<Vaccination>> getReminders() {
        return ResponseEntity.ok(vaccinationService.getUpcomingReminders());
    }
}
