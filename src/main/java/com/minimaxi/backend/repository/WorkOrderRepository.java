package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByMachineIdOrderByCreatedAtDesc(Long machineId);
}