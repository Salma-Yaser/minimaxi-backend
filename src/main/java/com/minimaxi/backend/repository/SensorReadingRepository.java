package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorMachineIdOrderByReadingTimeDesc(Long machineId);

    void deleteBySensorMachineId(Long machineId);

    List<SensorReading> findBySensor_Machine_OrganizationId(Long organizationId);

    List<SensorReading> findBySensorMachineIdAndReadingTimeAfterOrderByReadingTimeAsc(
            Long machineId,
            Instant after
    );

    /**
     * Bulk cleanup used by the retention scheduler (purges readings older
     * than the retention window so the table doesn't grow forever).
     * Returns the number of rows deleted so it can be logged.
     */
    long deleteByReadingTimeBefore(Instant cutoff);
}