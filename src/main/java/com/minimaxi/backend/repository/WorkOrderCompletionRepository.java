package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrderCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderCompletionRepository extends JpaRepository<WorkOrderCompletion, Long> {
}