package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.dto.response.NotificationResponse;
import com.minimaxi.backend.entity.Notification;
import com.minimaxi.backend.mapper.NotificationMapper;
import com.minimaxi.backend.repository.NotificationRepository;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markNotificationRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setIsRead(true);

        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void markAllNotificationsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
}