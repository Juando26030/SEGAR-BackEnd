package com.segar.backend.tramites;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.tramites")
@EnableJpaRepositories(basePackages = "com.segar.backend.tramites.infrastructure")
@EnableTransactionManagement
@ConfigurationPropertiesScan
public class TramitesModule {

    public static final String MODULE_NAME = "tramites";

    // Configuraciones específicas del módulo pueden ir aquí
}
