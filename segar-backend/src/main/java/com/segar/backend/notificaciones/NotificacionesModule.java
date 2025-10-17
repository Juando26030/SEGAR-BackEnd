package com.segar.backend.notificaciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración del módulo de Notificaciones
 * Maneja el envío de notificaciones y sistema completo de correos electrónicos
 */
@Configuration
@ComponentScan(basePackages = "com.segar.backend.notificaciones")
@EnableJpaRepositories(basePackages = "com.segar.backend.notificaciones.domain")
@EnableTransactionManagement
public class NotificacionesModule {

    // Este módulo gestiona:
    // - Sistema completo de correos electrónicos (envío y recepción)
    // - Envío de notificaciones por diferentes canales
    // - Preferencias de notificación de usuarios
    // - Tipos de notificación del sistema
    // - Historial de notificaciones
    // - Gestión de archivos adjuntos e imágenes embebidas
}
