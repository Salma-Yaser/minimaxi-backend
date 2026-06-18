package com.minimaxi.backend.service.impl;

import com.minimaxi.backend.config.NotificationWebSocketHandler;
import com.minimaxi.backend.dto.response.NotificationResponse;
import com.minimaxi.backend.entity.*;
import com.minimaxi.backend.enums.NotificationType;
import com.minimaxi.backend.enums.UserRole;
import com.minimaxi.backend.mapper.NotificationMapper;
import com.minimaxi.backend.repository.AppUserRepository;
import com.minimaxi.backend.repository.NotificationRepository;
import com.minimaxi.backend.repository.UserAssetAssignmentRepository;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;
    private final UserAssetAssignmentRepository userAssetAssignmentRepository;

    private final NotificationWebSocketHandler webSocketHandler;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   AppUserRepository appUserRepository,
                                   UserAssetAssignmentRepository userAssetAssignmentRepository,
                                   NotificationWebSocketHandler webSocketHandler) {
        this.notificationRepository = notificationRepository;
        this.appUserRepository = appUserRepository;
        this.userAssetAssignmentRepository = userAssetAssignmentRepository;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
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

    @Override
    @Transactional
    public void notifyWorkOrderEvent(WorkOrder workOrder, AppUser recipient,
                                     NotificationType type, String title, String message) {
        if (recipient == null) return;

        Notification notification = new Notification();
        notification.setOrganization(workOrder.getOrganization());
        notification.setRecipientUser(recipient);
        notification.setWorkOrder(workOrder);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(Instant.now());

        Notification saved = notificationRepository.save(notification);

        // بعت real-time لو الـ user متصل
        try {
            NotificationResponse response = NotificationMapper.toResponse(saved);
            String json = objectMapper.writeValueAsString(response);
            webSocketHandler.sendToUser(recipient.getId(), json);
        } catch (Exception e) {
            // لو فشل الـ WebSocket مش مشكلة، الـ notification اتحفظت في الـ DB
        }
    }
    @Override
    @Transactional
    public void notifyCriticalPrediction(Prediction prediction) {
        Machine machine = prediction.getMachine();
        Organization organization = prediction.getOrganization();

        String title = "Critical Machine Alert";
        String message = "Machine \"" + machine.getName() + "\" reached CRITICAL status. " +
                (prediction.getExplanation() != null ? prediction.getExplanation() : "Immediate attention required.");

        // 1. Notify all admins/engineers in the organization
        List<AppUser> staff = appUserRepository.findByOrganizationIdAndRoleIn(
                organization.getId(),
                List.of(UserRole.COMPANY_ADMIN, UserRole.SYSTEM_ADMIN, UserRole.ENGINEER)
        );

        for (AppUser user : staff) {
            saveNotification(organization, user, machine, prediction, NotificationType.SENSOR_ALERT, title, message);
        }

        // 2. Notify technician(s) assigned to this machine
        List<UserAssetAssignment> assignments = userAssetAssignmentRepository.findByMachineId(machine.getId());
        for (UserAssetAssignment assignment : assignments) {
            AppUser technician = assignment.getUser();
            saveNotification(organization, technician, machine, prediction, NotificationType.SENSOR_ALERT, title, message);
        }
    }

    private void saveNotification(Organization organization, AppUser recipient, Machine machine,
                                  Prediction prediction, NotificationType type, String title, String message) {
        Notification notification = new Notification();
        notification.setOrganization(organization);
        notification.setRecipientUser(recipient);
        notification.setMachine(machine);
        notification.setPrediction(prediction);
        notification.setType(type);
        notification.setSeverity(prediction.getSeverity());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(Instant.now());

        Notification saved = notificationRepository.save(notification);

        try {
            NotificationResponse response = NotificationMapper.toResponse(saved);
            String json = objectMapper.writeValueAsString(response);
            webSocketHandler.sendToUser(recipient.getId(), json);
        } catch (Exception e) {
            // لو الـ WebSocket فشل مش مشكلة، الـ DB اتحفظت
        }
    }
}