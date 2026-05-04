package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SensorTypeRepository extends JpaRepository<SensorType, Long> {
    List<SensorType> findByOrganizationId(Long organizationId);
}