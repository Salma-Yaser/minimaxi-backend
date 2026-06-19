package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Issue;
import com.minimaxi.backend.enums.IssueSource;
import com.minimaxi.backend.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByMachineIdOrderByCreatedAtDesc(Long machineId);

    // يستخدم في الـ duplicate-check قبل إنشاء AI issue جديدة لنفس الماكينة
    Optional<Issue> findFirstByMachineIdAndStatusInOrderByCreatedAtDesc(
            Long machineId, List<IssueStatus> statuses);

    // يستخدم في AI Insights على الداشبورد
    List<Issue> findByMachine_OrganizationIdAndSourceOrderByCreatedAtDesc(
            Long organizationId, IssueSource source);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM issue WHERE prediction_id IN (SELECT id FROM prediction WHERE machine_id = :machineId)", nativeQuery = true)
    void deleteByPredictionMachineId(@Param("machineId") Long machineId);
}