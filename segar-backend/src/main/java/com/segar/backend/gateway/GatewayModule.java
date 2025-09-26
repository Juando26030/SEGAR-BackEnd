package com.segar.backend.gateway;

import com.segar.backend.shared.SharedModule;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.gateway")
@EnableJpaRepositories(basePackages = "com.segar.backend.gateway.controllers")
@EnableTransactionManagement
@ConfigurationPropertiesScan
@Import(SharedModule.class)
public class GatewayModule {

    public static final String MODULE_NAME = "gateway";
}

