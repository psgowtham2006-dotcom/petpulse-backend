package com.petpulse.controller;

import com.petpulse.entity.AdoptionRequest;
import com.petpulse.service.AdoptionService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/adoptions")
@Tag(name = "Adoptions", description = "Adoption requests and approval workflow")
@SecurityRequirement(name = "bearerAuth")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping
    @Operation(summary = "List adoption requests", description = "Returns all adoption requests")
    public ResponseEntity<List<AdoptionRequest>> getAllRequests() {
        return ResponseEntity.ok(adoptionService.getAllRequests());
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Create adoption request", description = "Creates a pending adoption request")
    public ResponseEntity<AdoptionRequest> createRequest(@Valid @RequestBody AdoptionRequest request) {
        return ResponseEntity.ok(adoptionService.createRequest(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SHELTER') or hasRole('ADMIN')")
    @Operation(summary = "Update adoption request", description = "Approves or rejects an adoption request")
    public ResponseEntity<AdoptionRequest> updateRequest(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(adoptionService.updateRequestStatus(id, status));
    }
}
