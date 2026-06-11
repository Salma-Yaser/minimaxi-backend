package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Optional<Machine> findByAssetId(String assetId);
    List<Machine> findByOrganizationId(Long organizationId);
}