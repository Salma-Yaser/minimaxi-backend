package com.minimaxi.backend.service;

import com.minimaxi.backend.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotifications(Long userId);
    NotificationResponse markNotificationRead(Long id);
    void markAllNotificationsRead(Long userId);
}