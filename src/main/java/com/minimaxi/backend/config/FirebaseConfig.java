package com.minimaxi.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    // اسم الملف لازم يكون موجود في src/main/resources/
    private static final String SERVICE_ACCOUNT_FILE = "firebase-service-account.json";

    @PostConstruct
    public void initialize() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return; // اتعمله initialize قبل كده، منعملوش تاني
            }

            ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_FILE);

            if (!resource.exists()) {
                // الملف لسه مش موجود -> منوقفش تشغيل السيرفر بسببه
                // بس الـ push notifications مش هتشتغل لحد ما الملف يتحط
                System.err.println("[FirebaseConfig] " + SERVICE_ACCOUNT_FILE +
                        " not found in resources. Push notifications will be disabled until it's added.");
                return;
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("[FirebaseConfig] Firebase initialized successfully.");
            }

        } catch (IOException e) {
            // منرميش exception توقف السيرفر -> بس بنلوج المشكلة
            System.err.println("[FirebaseConfig] Firebase init failed: " + e.getMessage());
        }
    }
}