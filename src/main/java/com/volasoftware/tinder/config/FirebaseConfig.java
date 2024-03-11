package com.volasoftware.tinder.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.volasoftware.tinder.utility.FirebaseStorageUploader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Bean
    public Storage storage() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/tinder-d7708-firebase-adminsdk-b5es5-507c79cb65.json");

            StorageOptions options = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("src/main/resources/tinder-d7708-firebase-adminsdk-b5es5-507c79cb65.json")
                    .build();

            return options.getService();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating 'storage' bean", e);
        }
    }

    @Bean
    public FirebaseStorageUploader firebaseStorageUploader() throws IOException {
        return new FirebaseStorageUploader(storage());
    }
}
