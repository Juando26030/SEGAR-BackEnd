package com.segar.backend.notificaciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Configuración del módulo de Notificaciones
 * Maneja el envío de notificaciones y preferencias de usuarios
 */
@Configuration
@ComponentScan(basePackages = "com.segar.backend.notificaciones")
public class NotificacionesModule {

    // Este módulo gestiona:
    // - Envío de notificaciones por diferentes canales
    // - Preferencias de notificación de usuarios
    // - Tipos de notificación del sistema
    // - Historial de notificaciones
}
