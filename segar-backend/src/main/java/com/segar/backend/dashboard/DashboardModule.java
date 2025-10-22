package com.segar.backend.dashboard;

import com.segar.backend.shared.SharedModule;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.segar.backend.dashboard")
@EnableJpaRepositories(basePackages = "com.segar.backend.dashboard.infrastructure")
@EnableTransactionManagement
@ConfigurationPropertiesScan
@Import(SharedModule.class)
public class DashboardModule {

    public static final String MODULE_NAME = "dashboard";
}
