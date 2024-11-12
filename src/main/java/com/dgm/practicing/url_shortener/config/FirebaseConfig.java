package com.dgm.practicing.url_shortener.config;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    private static final Logger logger =LogManager.getLogger(FirebaseConfig.class);
    
    @Bean
    public Firestore firestore() {
        try {
            logger.info("Starting Firebase configuration...");

            InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
            logger.info("Firebase credentials file loaded successfully.");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase has been initialized successfully.");
            } else {
                logger.warn("Firebase was already initialized.");
            }
            return FirestoreClient.getFirestore();
        } catch (IOException ioe) {
            logger.error("Failed to initialize Firebase: {}", ioe.getMessage(), ioe);
            throw new RuntimeException("Error initializing Firebase", ioe);
        }
    }

}
