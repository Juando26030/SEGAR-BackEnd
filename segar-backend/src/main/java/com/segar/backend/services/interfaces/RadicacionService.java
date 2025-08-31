package com.segar.backend.services.interfaces;

import com.segar.backend.dto.RadicacionSolicitudDTO;
import com.segar.backend.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.models.Solicitud;

import java.util.List;
import java.util.Map;

/**
 * Interface del servicio para el Paso 5: Radicación de la Solicitud
 *
 * Define los métodos necesarios para el proceso de radicación formal
 * de solicitudes ante INVIMA con todas las validaciones requeridas.
 */
public interface RadicacionService {

    /**
     * Radica formalmente una solicitud ante INVIMA
     *
     * Validaciones previas:
     * - Empresa registrada
     * - Documentos obligatorios cargados
     * - Pago registrado y aprobado
     *
     * Procesamiento:
     * - Genera número de radicado único
     * - Cambia estado a RADICADA
     * - Registra fecha/hora
     * - Persiste en BD
     *
     * @param radicacionDTO Datos de la solicitud a radicar
     * @return Respuesta con número de radicado y detalles
     */
    SolicitudRadicadaResponseDTO radicarSolicitud(RadicacionSolicitudDTO radicacionDTO);

    /**
     * Obtiene todas las solicitudes radicadas de una empresa
     *
     * @param empresaId ID de la empresa
     * @return Lista de solicitudes radicadas
     */
    List<Solicitud> obtenerSolicitudesRadicadas(Long empresaId);

    /**
     * Valida los pre-requisitos para radicación
     *
     * @param empresaId ID de la empresa
     * @return Mapa con el estado de las validaciones
     */
    Map<String, Object> validarPreRequisitos(Long empresaId);

    /**
     * Busca una solicitud por su número de radicado
     *
     * @param numeroRadicado Número de radicado único
     * @return Solicitud encontrada
     */
    Solicitud buscarPorNumeroRadicado(String numeroRadicado);

    /**
     * Genera un número de radicado único
     * Formato: INV-2025-000123
     *
     * @return Número de radicado generado
     */
    String generarNumeroRadicado();
}
