package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.AlertResponse;
import com.minimaxi.backend.entity.Notification;
import com.minimaxi.backend.enums.NotificationType;
import com.minimaxi.backend.repository.NotificationRepository;
import com.minimaxi.backend.service.AlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AlertServiceImpl implements AlertService {

    private final NotificationRepository notificationRepository;

    public AlertServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // ─── helper: entity → AlertResponse ─────────────────────────────────────
    private AlertResponse toResponse(Notification n) {
        String type = switch (n.getType().name()) {
            case "PREDICTED_FAILURE" -> "prediction";
            case "SENSOR_ALERT"      -> "threshold";
            default                  -> "info";
        };

        Long machineId = null;
        String machineName = null;
        String assetId = null;

        try {
            if (n.getMachine() != null) {
                machineId = n.getMachine().getId();
                machineName = n.getMachine().getName();
                assetId = n.getMachine().getAssetId();
            }
        } catch (Exception e) {
            // lazy load فشل
        }

        return new AlertResponse(
                n.getId(),
                type,
                n.getSeverity() != null ? n.getSeverity().name().toLowerCase() : "info",
                machineId,
                machineName,
                assetId,
                n.getTitle(),
                n.getMessage(),
                n.getCreatedAt() != null ? n.getCreatedAt().toString() : null,
                Boolean.TRUE.equals(n.getAcknowledged()),
                n.getAcknowledgedBy(),
                n.getAcknowledgedAt() != null ? n.getAcknowledgedAt().toString() : null
        );
    }



    // ─── ACKNOWLEDGE ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AlertResponse acknowledgeAlert(Long id, String acknowledgedBy) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));

        notification.setAcknowledged(true);
        notification.setAcknowledgedBy(acknowledgedBy);
        notification.setAcknowledgedAt(Instant.now());

        return toResponse(notificationRepository.save(notification));
    }


    @Override
    @Transactional
    public List<AlertResponse> getAlerts(String severity, Boolean acknowledged, Long organizationId) {
        return notificationRepository
                .findByOrganizationIdAndTypeIn(
                        organizationId,
                        List.of(NotificationType.SENSOR_ALERT, NotificationType.PREDICTED_FAILURE)
                )
                .stream()
                .filter(n -> severity == null || severity.isBlank() ||
                        (n.getSeverity() != null && n.getSeverity().name().equalsIgnoreCase(severity)))
                .filter(n -> acknowledged == null ||
                        Boolean.TRUE.equals(n.getAcknowledged()) == acknowledged)
                .map(this::toResponse)
                .toList();
    }
}