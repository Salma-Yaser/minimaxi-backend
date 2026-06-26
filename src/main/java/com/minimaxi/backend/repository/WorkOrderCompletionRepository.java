package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrderCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkOrderCompletionRepository extends JpaRepository<WorkOrderCompletion, Long> {


    @Query("SELECT DISTINCT woc FROM WorkOrderCompletion woc " +
            "JOIN FETCH woc.workOrder wo " +
            "LEFT JOIN FETCH woc.sparePartsList " +
            "WHERE wo.machine.id = :machineId " +
            "ORDER BY woc.completedAt DESC")
    List<WorkOrderCompletion> findByMachineId(@Param("machineId") Long machineId);
}