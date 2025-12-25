package com.bcv.world.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials}")
    private String firebaseEncodedCredentials; // Base64 string from env or properties

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                byte[] decodedBytes = Base64.getDecoder().decode(firebaseEncodedCredentials);
                ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedBytes);

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://bcvworld-cc40e-default-rtdb.firebaseio.com") // ✅ Update with your DB URL
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully.");
            } else {
                System.out.println("⚠️ Firebase already initialized.");
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to initialize Firebase", e);
        }
    }
}
