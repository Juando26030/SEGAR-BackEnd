package com.segar.backend.services.interfaces;

import com.segar.backend.dto.RegistroSanitarioDTO;
import java.util.List;

/**
 * Servicio para gestión de registros sanitarios
 */
public interface RegistroSanitarioService {

    RegistroSanitarioDTO generarRegistroSanitario(Long resolucionId, Long productoId, Long empresaId);

    RegistroSanitarioDTO obtenerRegistroPorTramite(Long tramiteId);

    String generarNumeroRegistro();

    List<RegistroSanitarioDTO> obtenerRegistrosPorEmpresa(Long empresaId);
}
