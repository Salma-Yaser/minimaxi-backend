package com.minimaxi.backend.controller;

import com.minimaxi.backend.dto.response.NotificationResponse;
import com.minimaxi.backend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestParam("userId") Long userId) {
        return notificationService.getNotifications(userId);
    }

    @PostMapping("/{id}/read")
    public NotificationResponse markNotificationRead(@PathVariable Long id) {
        return notificationService.markNotificationRead(id);
    }

    @PostMapping("/read-all")
    public Map<String, Object> markAllNotificationsRead(@RequestParam("userId") Long userId) {
        notificationService.markAllNotificationsRead(userId);
        return Map.of("success", true);
    }
}