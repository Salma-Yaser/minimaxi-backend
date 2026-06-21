package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByMachineIdOrderByCreatedAtDesc(Long machineId);
    List<WorkOrder> findByMachine_OrganizationId(Long organizationId);

    @Query("SELECT w FROM WorkOrder w WHERE w.estimatedHours IS NOT NULL " +
            "AND w.assignedToUser IS NOT NULL " +
            "AND w.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<WorkOrder> findActiveWorkOrdersWithEstimatedHours();

    // للكالندر: work orders في شهر معين، لمنظمة معينة، بحالة محددة
    List<WorkOrder> findByOrganization_IdAndDueDateBetweenAndStatusIn(
            Long organizationId, LocalDate start, LocalDate end, List<WorkOrderStatus> statuses);

    // نفس الحاجة لكن من غير فلتر organization (لو الـ JWT ملوش org)
    List<WorkOrder> findByDueDateBetweenAndStatusIn(
            LocalDate start, LocalDate end, List<WorkOrderStatus> statuses);
}