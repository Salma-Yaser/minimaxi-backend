package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorMachineIdOrderByReadingTimeDesc(Long machineId);
}