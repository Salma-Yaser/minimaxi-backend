package com.minimaxi.backend.mapper;

import com.minimaxi.backend.dto.response.NotificationResponse;
import com.minimaxi.backend.entity.Notification;

public class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(
                        notification.getType() != null
                                ? notification.getType().name().toLowerCase()
                                : null
                )
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.getIsRead())
                .createdAt(
                        notification.getCreatedAt() != null
                                ? notification.getCreatedAt().toString()
                                : null
                )
                .severity(
                        notification.getSeverity() != null
                                ? notification.getSeverity().name().toLowerCase()
                                : null
                )
                .machineId(
                        notification.getMachine() != null
                                ? notification.getMachine().getId()
                                : null
                )
                .machineName(
                        notification.getMachine() != null
                                ? notification.getMachine().getName()
                                : null
                )
                .workOrderId(
                        notification.getWorkOrder() != null
                                ? notification.getWorkOrder().getId()
                                : null
                )
                .build();
    }
}