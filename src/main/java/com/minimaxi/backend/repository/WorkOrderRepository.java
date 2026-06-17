package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByMachineIdOrderByCreatedAtDesc(Long machineId);
    List<WorkOrder> findByMachine_OrganizationId(Long organizationId);
    @Query("SELECT w FROM WorkOrder w WHERE w.estimatedHours IS NOT NULL " +
            "AND w.assignedToUser IS NOT NULL " +
            "AND w.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<WorkOrder> findActiveWorkOrdersWithEstimatedHours();
}