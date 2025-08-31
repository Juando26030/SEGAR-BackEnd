package com.segar.backend.services.interfaces;

import com.segar.backend.dto.HistorialTramiteDTO;
import java.util.List;

/**
 * Servicio para gestión de historial de trámites
 */
public interface HistorialTramiteService {

    void registrarEvento(Long tramiteId, String accion, String descripcion, String usuario, String estado);

    List<HistorialTramiteDTO> obtenerHistorialPorTramite(Long tramiteId);
}
