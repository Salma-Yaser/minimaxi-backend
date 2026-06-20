package com.minimaxi.backend.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PushNotificationService {

    /**
     * بيبعت Push Notification لجهاز واحد عن طريق الـ FCM token بتاعه.
     * لو الـ token فاضي، أو Firebase لسه مش متهيأ (ملف الـ service account
     * لسه مش موجود)، أو حصل أي error من Firebase -> بنعمل log بس ومنرميش
     * exception، عشان مننوقفش باقي الـ flow (notification + WebSocket).
     */
    public void sendPush(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            return; // اليوزر معندوش token محفوظ (مفتحش الـ app من على جهازه لسه)
        }

        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase لسه مش متهيأ -> على الأغلب ملف service account لسه مش موجود
            System.err.println("[PushNotificationService] Firebase not initialized, skipping push.");
            return;
        }

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            // log error بس -> لو فشلت notification واحدة متوقفش باقي الـ flow
            System.err.println("[PushNotificationService] Failed to send push: " + e.getMessage());
        }
    }
}