package com.segar.backend.shared;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.shared")
@EnableJpaRepositories(basePackages = "com.segar.backend.shared.infrastructure")
@EnableTransactionManagement
@ConfigurationPropertiesScan
public class SharedModule {

    public static final String MODULE_NAME = "shared";
}
