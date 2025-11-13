package com.segar.backend.config;

import com.google.cloud.storage.Storage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * Configuración de pruebas para Google Cloud Storage
 * Proporciona un mock del servicio Storage para evitar la necesidad del archivo gcp-service-account.json
 */
@TestConfiguration
@Profile("test")
public class TestGoogleCloudConfig {

    @Bean
    @Primary
    public Storage storage() {
        // Retorna un mock de Storage para que los tests de integración no fallen
        return mock(Storage.class);
    }
}
