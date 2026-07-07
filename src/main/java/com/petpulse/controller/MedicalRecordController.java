package com.petpulse.controller;

import com.petpulse.entity.MedicalRecord;
import com.petpulse.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records", description = "Veterinary records and approval")
@SecurityRequirement(name = "bearerAuth")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    @Operation(summary = "List medical records", description = "Returns all medical records")
    public ResponseEntity<List<MedicalRecord>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    @PostMapping
    @PreAuthorize("hasRole('VET') or hasRole('ADMIN')")
    @Operation(summary = "Create medical record", description = "Adds a pending medical record")
    public ResponseEntity<MedicalRecord> createRecord(@Valid @RequestBody MedicalRecord record) {
        return ResponseEntity.ok(medicalRecordService.saveRecord(record));
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Pet medical records", description = "Returns records for a specific pet")
    public ResponseEntity<List<MedicalRecord>> getRecordsByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPetId(petId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('VET') or hasRole('ADMIN')")
    @Operation(summary = "Approve medical record", description = "Sets a medical record status to APPROVED")
    public ResponseEntity<MedicalRecord> approveRecord(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.approveRecord(id));
    }
}
