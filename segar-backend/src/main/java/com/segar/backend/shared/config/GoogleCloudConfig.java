package com.segar.backend.shared.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GoogleCloudConfig {


    @Bean
    public Storage storage() throws IOException {
        ClassPathResource resource = new ClassPathResource("gcp-service-account.json");
        return StorageOptions.newBuilder()
                .setProjectId("segar-cloud-473618")
                .setCredentials(ServiceAccountCredentials.fromStream(resource.getInputStream()))
                .build()
                .getService();
    }

}
