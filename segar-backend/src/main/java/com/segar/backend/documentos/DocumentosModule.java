package com.segar.backend.documentos;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.documentos")
@EnableJpaRepositories(basePackages = "com.segar.backend.documentos.infrastructure")
@EnableTransactionManagement
@ConfigurationPropertiesScan
public class DocumentosModule {

    public static final String MODULE_NAME = "documentos";

    // Configuraciones específicas del módulo pueden ir aquí
}
