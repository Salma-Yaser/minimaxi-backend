package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findTopByMachineIdOrderByPredictedAtDesc(Long machineId);

    interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

        List<SensorReading> findBySensorMachineIdOrderByTimestampDesc(Long machineId);
    }
}
