package com.minimaxi.backend.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PushNotificationService {

    public void sendPush(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            System.out.println("[PushNotificationService] SKIPPED — fcmToken is null/blank for this recipient.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            System.err.println("[PushNotificationService] SKIPPED — Firebase not initialized.");
            return;
        }

        System.out.println("[PushNotificationService] Attempting send. token=" + fcmToken
                + " title=" + title);

        Message message = Message.builder()
                .setToken(fcmToken)
                // ✅ لا يوجد setNotification() — data-only payload
                // عشان Flutter يتحكم في العرض ويستخدم أيقونة MiniMaxi
                .putData("title", title)
                .putData("body", body)
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("[PushNotificationService] SUCCESS — FCM message id: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("[PushNotificationService] FAILED — code=" + e.getMessagingErrorCode()
                    + " message=" + e.getMessage());
        }
    }
}