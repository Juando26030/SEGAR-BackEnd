package com.segar.backend.calendario.service;

import com.segar.backend.calendario.api.dto.EstadisticasCalendarioDTO;
import com.segar.backend.calendario.domain.CategoriaEvento;
import com.segar.backend.calendario.domain.EstadoEvento;
import com.segar.backend.calendario.domain.PrioridadEvento;
import com.segar.backend.calendario.domain.TipoEvento;
import com.segar.backend.calendario.infrastructure.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarioService {

    private final EventoRepository eventoRepository;

    public EstadisticasCalendarioDTO obtenerEstadisticas() {
        return EstadisticasCalendarioDTO.builder()
                .totalEventos(eventoRepository.count())
                .eventosCriticos(eventoRepository.countEventosCriticos())
                .eventosCompletados(eventoRepository.countByEstado(EstadoEvento.COMPLETADO))
                .eventosVencidos(eventoRepository.countEventosVencidos())
                .eventosActivos(eventoRepository.countByEstado(EstadoEvento.ACTIVO))
                .build();
    }

    //Estadísticas por empresa
    public EstadisticasCalendarioDTO obtenerEstadisticasPorEmpresa(Long empresaId) {
        return EstadisticasCalendarioDTO.builder()
                .totalEventos(eventoRepository.findByEmpresaId(empresaId).size())
                .eventosCriticos(eventoRepository.countEventosCriticosByEmpresaId(empresaId))
                .eventosCompletados(eventoRepository.countByEmpresaIdAndEstado(empresaId, EstadoEvento.COMPLETADO))
                .eventosVencidos(eventoRepository.countEventosVencidosByEmpresaId(empresaId))
                .eventosActivos(eventoRepository.countByEmpresaIdAndEstado(empresaId, EstadoEvento.ACTIVO))
                .build();
    }

    // Estadísticas por usuario
    public EstadisticasCalendarioDTO obtenerEstadisticasPorUsuario(Long usuarioId) {
        return EstadisticasCalendarioDTO.builder()
                .totalEventos(eventoRepository.findByUsuarioId(usuarioId).size())
                .eventosCriticos(eventoRepository.countEventosCriticosByUsuarioId(usuarioId))
                .eventosCompletados(eventoRepository.countByUsuarioIdAndEstado(usuarioId, EstadoEvento.COMPLETADO))
                .eventosVencidos(eventoRepository.countEventosVencidosByUsuarioId(usuarioId))
                .eventosActivos(eventoRepository.countByUsuarioIdAndEstado(usuarioId, EstadoEvento.ACTIVO))
                .build();
    }

    public List<TipoEvento> obtenerTiposEvento() {
        return Arrays.asList(TipoEvento.values());
    }

    public List<CategoriaEvento> obtenerCategoriasEvento() {
        return Arrays.asList(CategoriaEvento.values());
    }

    public List<PrioridadEvento> obtenerPrioridadesEvento() {
        return Arrays.asList(PrioridadEvento.values());
    }

    public List<EstadoEvento> obtenerEstadosEvento() {
        return Arrays.asList(EstadoEvento.values());
    }
}
