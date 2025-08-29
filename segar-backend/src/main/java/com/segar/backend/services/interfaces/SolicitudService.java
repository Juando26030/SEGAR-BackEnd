package com.segar.backend.services.interfaces;

import com.segar.backend.dto.RadicacionSolicitudDTO;
import com.segar.backend.dto.SolicitudRadicadaResponseDTO;
import com.segar.backend.models.Solicitud;
import com.segar.backend.models.EstadoSolicitud;

import java.util.List;

/**
 * Interfaz del servicio para gestión de solicitudes de trámites INVIMA
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
public interface SolicitudService {

    /**
     * Radica una nueva solicitud de trámite ante INVIMA
     * @param radicacionDTO Datos de la solicitud a radicar
     * @return DTO con la información de la solicitud radicada
     * @throws DocumentosIncompletosException Si faltan documentos obligatorios
     * @throws PagoInvalidoException Si no existe pago válido
     * @throws SolicitudDuplicadaException Si ya existe solicitud para el mismo producto y trámite
     */
    SolicitudRadicadaResponseDTO radicarSolicitud(RadicacionSolicitudDTO radicacionDTO);

    /**
     * Obtiene todas las solicitudes de una empresa
     * @param empresaId ID de la empresa
     * @return Lista de solicitudes
     */
    List<Solicitud> obtenerSolicitudesPorEmpresa(Long empresaId);

    /**
     * Obtiene solicitudes por estado
     * @param estado Estado de las solicitudes
     * @return Lista de solicitudes filtradas
     */
    List<Solicitud> obtenerSolicitudesPorEstado(EstadoSolicitud estado);

    /**
     * Busca una solicitud por su número de radicado
     * @param numeroRadicado Número de radicado
     * @return Solicitud encontrada
     */
    Solicitud buscarPorNumeroRadicado(String numeroRadicado);

    /**
     * Obtiene una solicitud por su ID
     * @param id ID de la solicitud
     * @return Solicitud encontrada
     */
    Solicitud obtenerSolicitudPorId(Long id);
}
