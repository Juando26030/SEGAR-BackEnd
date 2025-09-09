package com.segar.backend.notificaciones.service;

import com.segar.backend.notificaciones.domain.*;
import com.segar.backend.notificaciones.infrastructure.NotificacionRepository;
import com.segar.backend.notificaciones.infrastructure.PreferenciasNotificacionRepository;
import com.segar.backend.shared.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación para la gestión de notificaciones
 */
@Service
@Transactional
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final PreferenciasNotificacionRepository preferenciasRepository;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository,
                              PreferenciasNotificacionRepository preferenciasRepository) {
        this.notificacionRepository = notificacionRepository;
        this.preferenciasRepository = preferenciasRepository;
    }

    /**
     * Caso de uso: Enviar una notificación
     */
    public Notificacion enviarNotificacion(TipoNotificacion tipo, String titulo, String mensaje,
                                          String destinatario, CanalNotificacion canal, Long tramiteId) {
        // Verificar preferencias del usuario
        PreferenciasNotificacion preferencias = obtenerPreferenciasUsuario(destinatario);

        if (preferencias != null && !preferencias.esTipoHabilitado(tipo)) {
            return null; // Usuario no quiere este tipo de notificación
        }

        if (preferencias != null && !preferencias.esCanalHabilitado(canal)) {
            return null; // Usuario no quiere notificaciones por este canal
        }

        // Crear notificación usando método de dominio
        Notificacion notificacion = Notificacion.crear(tipo, titulo, mensaje, destinatario, canal, tramiteId);

        // Verificar horario permitido
        if (!notificacion.estaEnHorarioPermitido(preferencias)) {
            return null; // Fuera del horario permitido
        }

        // Guardar y intentar enviar
        Notificacion guardada = notificacionRepository.save(notificacion);

        try {
            enviarPorCanal(guardada, canal);
            guardada.marcarComoEnviada();
        } catch (Exception e) {
            guardada.registrarFallo("Error al enviar: " + e.getMessage());
        }

        return notificacionRepository.save(guardada);
    }

    /**
     * Caso de uso: Configurar preferencias de usuario
     */
    public void configurarPreferencias(String usuarioId, PreferenciasNotificacion preferencias) {
        PreferenciasNotificacion existente = preferenciasRepository.findByUsuarioId(usuarioId).orElse(null);

        if (existente != null) {
            // Actualizar preferencias existentes usando métodos de dominio
            existente.actualizar(preferencias);
            preferenciasRepository.save(existente);
        } else {
            // Crear nuevas preferencias
            preferencias.setUsuarioId(usuarioId);
            preferenciasRepository.save(preferencias);
        }
    }

    /**
     * Consulta: Obtener preferencias de usuario
     */
    @Transactional(readOnly = true)
    public PreferenciasNotificacion obtenerPreferenciasUsuario(String usuarioId) {
        return preferenciasRepository.findByUsuarioId(usuarioId).orElse(null);
    }

    /**
     * Consulta: Obtener notificaciones de usuario con paginación
     */
    @Transactional(readOnly = true)
    public Page<Notificacion> obtenerNotificacionesUsuario(String usuarioId, Pageable pageable, boolean soloNoLeidas) {
        if (soloNoLeidas) {
            return notificacionRepository.findAll(pageable); // TODO: Implementar filtro específico
        }
        return notificacionRepository.findByDestinatario(usuarioId, pageable);
    }

    /**
     * Caso de uso: Marcar notificación como leída
     */
    public void marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
            .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));

        // Usar método de dominio
        notificacion.marcarComoLeida();
        notificacionRepository.save(notificacion);
    }

    /**
     * Caso de uso: Marcar todas las notificaciones como leídas
     */
    public int marcarTodasComoLeidas(String usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository.findNoLeidasByDestinatario(usuarioId);

        // Usar métodos de dominio
        for (Notificacion notificacion : noLeidas) {
            notificacion.marcarComoLeida();
        }

        notificacionRepository.saveAll(noLeidas);
        return noLeidas.size();
    }

    /**
     * Consulta: Obtener estadísticas de notificaciones del usuario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasUsuario(String usuarioId) {
        long totalNotificaciones = notificacionRepository.findByDestinatario(usuarioId).size();
        long noLeidas = notificacionRepository.countNoLeidasByDestinatario(usuarioId);
        long leidas = totalNotificaciones - noLeidas;

        return Map.of(
            "total", totalNotificaciones,
            "noLeidas", noLeidas,
            "leidas", leidas,
            "porcentajeLeidas", totalNotificaciones > 0 ? (leidas * 100.0 / totalNotificaciones) : 0
        );
    }

    /**
     * Caso de uso: Procesar notificaciones fallidas para reintento
     */
    public void procesarNotificacionesFallidas() {
        List<Notificacion> fallidas = notificacionRepository.findPendientesReenvio(
            EstadoNotificacion.FALLIDA, 3);

        for (Notificacion notificacion : fallidas) {
            if (notificacion.puedeSerReenviada(3)) {
                notificacion.prepararReintento();
                try {
                    enviarPorCanal(notificacion, notificacion.getCanal());
                    notificacion.marcarComoEnviada();
                } catch (Exception e) {
                    notificacion.registrarFallo("Reintento fallido: " + e.getMessage());
                }
                notificacionRepository.save(notificacion);
            }
        }
    }

    // ===============================
    // EVENT LISTENERS
    // ===============================

    @EventListener
    public void onTramiteCreado(TramiteCreadoEvent event) {
        enviarNotificacion(
            TipoNotificacion.TRAMITE_CREADO,
            "Nuevo Trámite Creado",
            String.format("Se ha creado un nuevo trámite de tipo %s con ID %d", event.tipoTramite(), event.tramiteId()),
            event.usuarioCreador(),
            CanalNotificacion.EMAIL,
            event.tramiteId()
        );
    }

    @EventListener
    public void onEstadoCambiado(TramiteEstadoCambiadoEvent event) {
        enviarNotificacion(
            TipoNotificacion.ESTADO_CAMBIADO,
            "Estado del Trámite Actualizado",
            String.format("El trámite %d ha cambiado de %s a %s",
                event.tramiteId(),
                event.estadoAnterior().getDescripcion(),
                event.estadoNuevo().getDescripcion()),
            "USUARIO", // TODO: Obtener usuario del trámite
            CanalNotificacion.EMAIL,
            event.tramiteId()
        );
    }

    @EventListener
    public void onRequerimientoCreado(RequerimientoCreadoEvent event) {
        enviarNotificacion(
            TipoNotificacion.NUEVO_REQUERIMIENTO,
            "Nuevo Requerimiento",
            String.format("Se ha generado un nuevo requerimiento para el trámite %d: %s",
                event.tramiteId(), event.descripcion()),
            event.usuarioDestino(),
            CanalNotificacion.EMAIL,
            event.tramiteId()
        );
    }

    // ===============================
    // MÉTODOS PRIVADOS
    // ===============================

    private void enviarPorCanal(Notificacion notificacion, CanalNotificacion canal) {
        switch (canal) {
            case EMAIL -> enviarPorEmail(notificacion);
            case SMS -> enviarPorSMS(notificacion);
            case PUSH -> enviarPushNotification(notificacion);
            case IN_APP -> marcarComoInApp(notificacion);
        }
    }

    private void enviarPorEmail(Notificacion notificacion) {
        // TODO: Implementar lógica de envío por email
        System.out.println("Enviando email a: " + notificacion.getDestinatario());
    }

    private void enviarPorSMS(Notificacion notificacion) {
        // TODO: Implementar lógica de envío por SMS
        System.out.println("Enviando SMS a: " + notificacion.getDestinatario());
    }

    private void enviarPushNotification(Notificacion notificacion) {
        // TODO: Implementar lógica de push notification
        System.out.println("Enviando push notification a: " + notificacion.getDestinatario());
    }

    private void marcarComoInApp(Notificacion notificacion) {
        // Las notificaciones in-app se marcan como enviadas automáticamente
        System.out.println("Notificación in-app creada para: " + notificacion.getDestinatario());
    }
}
