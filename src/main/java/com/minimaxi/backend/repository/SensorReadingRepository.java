package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorMachineIdOrderByReadingTimeDesc(Long machineId);

    void deleteByMachineId(Long machineId);
    List<SensorReading> findBySensor_Machine_OrganizationId(Long organizationId);
    List<SensorReading> findBySensorMachineIdAndReadingTimeAfterOrderByReadingTimeAsc(
            Long machineId,
            Instant after
    );
}