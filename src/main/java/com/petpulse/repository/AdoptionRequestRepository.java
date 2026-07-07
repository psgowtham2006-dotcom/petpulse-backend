package com.petpulse.repository;

import com.petpulse.entity.AdoptionRequest;
import com.petpulse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {
    List<AdoptionRequest> findByApplicant(User applicant);
}
