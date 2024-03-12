package com.volasoftware.tinder.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Value("${serviceAccountKeyPath}")
    private String serviceAccountKeyPath;

    @Value("${databaseUrl}")
    private String databaseUrl;

    @Bean
    public Storage storage() {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

            StorageOptions options = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(databaseUrl)
                    .build();

            return options.getService();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating 'storage' bean", e);
        }
    }
}
