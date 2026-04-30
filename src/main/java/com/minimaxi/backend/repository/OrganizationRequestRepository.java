package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.OrganizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest, Long> {
}