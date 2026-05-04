package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.AlertResponse;
import com.minimaxi.backend.entity.Notification;
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
        // نحدد الـ type من الـ notification type
        String type = switch (n.getType().name()) {
            case "PREDICTED_FAILURE" -> "prediction";
            case "SENSOR_ALERT"      -> "threshold";
            default                  -> "info";
        };

        return new AlertResponse(
                n.getId(),
                type,
                n.getSeverity() != null ? n.getSeverity().name().toLowerCase() : "info",
                n.getMachine() != null ? n.getMachine().getId() : null,
                n.getMachine() != null ? n.getMachine().getName() : null,
                n.getMachine() != null ? n.getMachine().getAssetId() : null,
                n.getTitle(),
                n.getMessage(),
                n.getCreatedAt() != null ? n.getCreatedAt().toString() : null,
                Boolean.TRUE.equals(n.getAcknowledged()),
                n.getAcknowledgedBy(),
                n.getAcknowledgedAt() != null ? n.getAcknowledgedAt().toString() : null
        );
    }

    // ─── GET ALL ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<AlertResponse> getAlerts(String severity, Boolean acknowledged) {
        return notificationRepository.findAll()
                .stream()
                // بنجيب بس الـ PREDICTED_FAILURE و SENSOR_ALERT كـ alerts
                .filter(n -> n.getType().name().equals("PREDICTED_FAILURE")
                        || n.getType().name().equals("SENSOR_ALERT"))
                .filter(n -> severity == null || severity.isBlank() ||
                        (n.getSeverity() != null && n.getSeverity().name().equalsIgnoreCase(severity)))
                .filter(n -> acknowledged == null ||
                        Boolean.TRUE.equals(n.getAcknowledged()) == acknowledged)
                .map(this::toResponse)
                .toList();
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
}