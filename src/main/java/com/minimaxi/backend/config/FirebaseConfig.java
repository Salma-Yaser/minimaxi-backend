package com.minimaxi.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    // اسم الملف لازم يكون موجود في src/main/resources/ (للتشغيل المحلي local)
    private static final String SERVICE_ACCOUNT_FILE = "firebase-service-account.json";

    // اسم الـ environment variable اللي هنحطه في Railway (للإنتاج)
    private static final String SERVICE_ACCOUNT_ENV_VAR = "FIREBASE_SERVICE_ACCOUNT_JSON";

    @PostConstruct
    public void initialize() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return; // اتعمله initialize قبل كده، منعملوش تاني
            }

            // 1) أول حاجة نجرب: الـ environment variable (ده اللي هيشتغل على Railway)
            String envJson = System.getenv(SERVICE_ACCOUNT_ENV_VAR);
            if (envJson != null && !envJson.isBlank()) {
                try (InputStream serviceAccount =
                             new ByteArrayInputStream(envJson.getBytes(StandardCharsets.UTF_8))) {

                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                    System.out.println("[FirebaseConfig] Firebase initialized successfully from ENV VAR.");
                    return;
                }
            }

            // 2) لو الـ env variable مش موجود، نجرب الملف المحلي (مفيد للتشغيل على الجهاز local)
            ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_FILE);

            if (!resource.exists()) {
                // مفيش لا env variable ولا ملف محلي -> منوقفش تشغيل السيرفر بسببه
                // بس الـ push notifications مش هتشتغل لحد ما حد منهم يتظبط
                System.err.println("[FirebaseConfig] Neither " + SERVICE_ACCOUNT_ENV_VAR +
                        " env var nor " + SERVICE_ACCOUNT_FILE +
                        " file found. Push notifications will be disabled.");
                return;
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("[FirebaseConfig] Firebase initialized successfully from local file.");
            }

        } catch (IOException e) {
            // منرميش exception توقف السيرفر -> بس بنلوج المشكلة
            System.err.println("[FirebaseConfig] Firebase init failed: " + e.getMessage());
        }
    }
}