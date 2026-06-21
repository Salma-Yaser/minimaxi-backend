package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrderSparePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderSparePartRepository extends JpaRepository<WorkOrderSparePart, Long> {

    List<WorkOrderSparePart> findByCompletionId(Long completionId);
}
