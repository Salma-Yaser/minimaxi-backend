package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.UserAssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAssetAssignmentRepository extends JpaRepository<UserAssetAssignment, Long> {
    List<UserAssetAssignment> findByMachineId(Long machineId);
}