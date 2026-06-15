package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByMachineIdOrderByCreatedAtDesc(Long machineId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM issue WHERE prediction_id IN (SELECT id FROM prediction WHERE machine_id = :machineId)", nativeQuery = true)
    void deleteByPredictionMachineId(@Param("machineId") Long machineId);
}