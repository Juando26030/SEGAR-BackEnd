package com.segar.backend.notificaciones.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio para sincronización automática programada de correos
 * Se puede habilitar/deshabilitar desde application.properties
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    value = "email.sync.scheduled.enabled",
    havingValue = "true"
)
public class EmailSyncScheduler {

    private final EmailService emailService;

    /**
     * Sincronización automática cada X minutos (configurable desde properties)
     * Por defecto cada 5 minutos (300000 ms)
     */
    @Scheduled(fixedDelayString = "${email.sync.scheduled.interval:300000}")
    public void scheduledEmailSync() {
        log.info("🕒 Iniciando sincronización programada automática de correos");
        try {
            emailService.synchronizeEmailsInternal();
            log.info("✅ Sincronización programada completada exitosamente");
        } catch (Exception e) {
            log.error("❌ Error en sincronización programada: {}", e.getMessage(), e);
        }
    }

    /**
     * Sincronización al iniciar la aplicación (opcional)
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void initialSync() {
        if (Boolean.parseBoolean(System.getProperty("email.sync.on.startup", "false"))) {
            log.info("🚀 Sincronización inicial al arrancar la aplicación");
            try {
                emailService.synchronizeEmailsAsync();
            } catch (Exception e) {
                log.error("❌ Error en sincronización inicial: {}", e.getMessage(), e);
            }
        }
    }
}
