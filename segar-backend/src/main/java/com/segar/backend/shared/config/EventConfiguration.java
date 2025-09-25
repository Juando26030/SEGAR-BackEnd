package com.segar.backend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para eventos asincrónicos entre módulos
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
public class EventConfiguration {

    @Bean(name = "moduleEventExecutor")
    public Executor moduleEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ModuleEvent-");
        executor.initialize();
        return executor;
    }
}
