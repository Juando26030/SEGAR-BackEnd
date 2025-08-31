package com.segar.backend.services.implementations;

import com.segar.backend.dto.RegistroSanitarioDTO;
import com.segar.backend.models.RegistroSanitario;
import com.segar.backend.repositories.RegistroSanitarioRepository;
import com.segar.backend.services.interfaces.RegistroSanitarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de registros sanitarios
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegistroSanitarioServiceImpl implements RegistroSanitarioService {

    private final RegistroSanitarioRepository registroSanitarioRepository;

    @Override
    public RegistroSanitarioDTO generarRegistroSanitario(Long resolucionId, Long productoId, Long empresaId) {
        LocalDateTime fechaExpedicion = LocalDateTime.now();
        LocalDateTime fechaVencimiento = fechaExpedicion.plusYears(5); // 5 años de vigencia

        RegistroSanitario registro = RegistroSanitario.builder()
            .numeroRegistro(generarNumeroRegistro())
            .fechaExpedicion(fechaExpedicion)
            .fechaVencimiento(fechaVencimiento)
            .productoId(productoId)
            .empresaId(empresaId)
            .estado(com.segar.backend.models.EstadoRegistro.VIGENTE)
            .resolucionId(resolucionId)
            .build();

        RegistroSanitario registroGuardado = registroSanitarioRepository.save(registro);
        return mapearADTO(registroGuardado);
    }

    @Override
    public RegistroSanitarioDTO obtenerRegistroPorTramite(Long tramiteId) {
        // Buscar registro por resolución asociada al trámite
        return registroSanitarioRepository.findByResolucionId(tramiteId)
            .map(this::mapearADTO)
            .orElse(null);
    }

    @Override
    public String generarNumeroRegistro() {
        int year = LocalDateTime.now().getYear();
        Long count = registroSanitarioRepository.countByYear(year) + 1;
        return String.format("RSAA21M-%d%04d", year, count);
    }

    @Override
    public List<RegistroSanitarioDTO> obtenerRegistrosPorEmpresa(Long empresaId) {
        return registroSanitarioRepository.findByEmpresaId(empresaId)
            .stream()
            .map(this::mapearADTO)
            .toList();
    }

    private RegistroSanitarioDTO mapearADTO(RegistroSanitario registro) {
        return RegistroSanitarioDTO.builder()
            .id(registro.getId())
            .numeroRegistro(registro.getNumeroRegistro())
            .fechaExpedicion(registro.getFechaExpedicion())
            .fechaVencimiento(registro.getFechaVencimiento())
            .productoId(registro.getProductoId())
            .empresaId(registro.getEmpresaId())
            .estado(registro.getEstado().name())
            .resolucionId(registro.getResolucionId())
            .documentoUrl(registro.getDocumentoUrl())
            .build();
    }
}
