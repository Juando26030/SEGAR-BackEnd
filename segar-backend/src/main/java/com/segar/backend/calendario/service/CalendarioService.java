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
