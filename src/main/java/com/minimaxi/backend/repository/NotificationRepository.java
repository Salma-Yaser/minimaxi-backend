package com.minimaxi.backend.repository;

import com.minimaxi.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);
    void deleteByMachineId(Long machineId);
    void deleteByWorkOrderId(Long workOrderId);
    void deleteByPredictionId(Long predictionId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.prediction.id IN (SELECT p.id FROM Prediction p WHERE p.machine.id = :machineId)")
    void deleteByPredictionMachineId(@Param("machineId") Long machineId);
}