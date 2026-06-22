package com.minimaxi.backend.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PushNotificationService {

    /**
     * بيبعت data-only Push Notification لجهاز واحد عن طريق الـ FCM token بتاعه.
     * title و body بيتبعتوا جوه data object بدل notification object —
     * عشان Flutter app تتحكم في عرض الإشعار وأيقونته بدل Android OS.
     */
    public void sendPush(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            System.out.println("[PushNotificationService] SKIPPED — fcmToken is null/blank for this recipient.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            System.err.println("[PushNotificationService] SKIPPED — Firebase not initialized.");
            return;
        }

        System.out.println("[PushNotificationService] Attempting send. token=" + fcmToken + " title=" + title);

        // بنبني data-only message — بدون .setNotification() خالص
        // title و body بيتحطوا جوه data عشان Flutter هي اللي تعرض الإشعار
        Message message = Message.builder()
                .setToken(fcmToken)
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