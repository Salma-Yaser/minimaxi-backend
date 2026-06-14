package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.NotificationResponse;
import com.minimaxi.backend.entity.AppUser;
import com.minimaxi.backend.entity.Prediction;
import com.minimaxi.backend.entity.WorkOrder;
import com.minimaxi.backend.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotifications(Long userId);
    NotificationResponse markNotificationRead(Long id);
    void markAllNotificationsRead(Long userId);

    void notifyWorkOrderEvent(WorkOrder workOrder, AppUser recipient, NotificationType type, String title, String message);
    void notifyCriticalPrediction(Prediction prediction);
}