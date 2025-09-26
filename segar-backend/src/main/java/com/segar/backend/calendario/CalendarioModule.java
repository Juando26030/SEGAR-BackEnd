package com.segar.backend.calendario;

import com.segar.backend.shared.SharedModule;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.calendario")
@EnableJpaRepositories(basePackages = "com.segar.backend.calendario.infrastructure")
@EnableTransactionManagement
@ConfigurationPropertiesScan
@Import(SharedModule.class)
public class CalendarioModule {

    public static final String MODULE_NAME = "calendario";

    // Configuraciones específicas del módulo pueden ir aquí
}
