package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Prediction p WHERE p.machine.id = :machineId")
    void deleteByMachineIdDirect(@Param("machineId") Long machineId);


    Optional<Prediction> findTopByMachineIdOrderByPredictedAtDesc(Long machineId);

    List<Prediction> findByMachineId(Long machineId);
    List<Prediction> findByMachine_OrganizationId(Long organizationId);
    interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

        List<SensorReading> findBySensorMachineIdOrderByTimestampDesc(Long machineId);
    }


    @Query("""
    SELECT p FROM Prediction p
    JOIN FETCH p.machine m
    LEFT JOIN FETCH m.assetType
    WHERE p.organization.id = :orgId
    AND p.ttfHours IS NOT NULL
    AND p.ttfHours <= :maxTtf
    AND p.id = (
        SELECT MAX(p2.id) FROM Prediction p2
        WHERE p2.machine.id = p.machine.id
    )
    """)
    List<Prediction> findLatestPredictionsWithTtfUnder(
            @Param("orgId") Long orgId,
            @Param("maxTtf") BigDecimal maxTtf);

    @Query("""
    SELECT p FROM Prediction p
    JOIN FETCH p.machine m
    LEFT JOIN FETCH m.assetType
    WHERE p.organization.id = :orgId
    AND p.ttfHours IS NOT NULL
    AND p.id = (
        SELECT MAX(p2.id) FROM Prediction p2
        WHERE p2.machine.id = p.machine.id
    )
    """)
    List<Prediction> findLatestPredictionsForOrg(@Param("orgId") Long orgId);
}
