package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.WorkOrderRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRatingRepository extends JpaRepository<WorkOrderRating, Long> {

    Optional<WorkOrderRating> findByWorkOrderId(Long workOrderId);

    List<WorkOrderRating> findByTechnicianUserId(Long technicianUserId);

    // عشان نحسب rating/success_rate لكل التكنيشن مرة واحدة في الـ reports
    List<WorkOrderRating> findByTechnicianUser_Organization_Id(Long organizationId);
}