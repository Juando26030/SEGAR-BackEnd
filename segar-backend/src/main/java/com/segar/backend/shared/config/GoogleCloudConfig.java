package com.segar.backend.shared.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudConfig {

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setProjectId("segar-cloud-473618")
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new FileInputStream("/Users/danielteran/Documents/tesis/SEGAR-BackEnd/segar-backend/gcp-service-account.json")))
                .build()
                .getService();
    }
}
