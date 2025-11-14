package com.segar.backend.calendario.service;

import com.segar.backend.calendario.api.dto.CrearEventoDTO;
import com.segar.backend.calendario.api.dto.EventoDTO;
import com.segar.backend.calendario.domain.EstadoEvento;
import com.segar.backend.calendario.domain.Evento;
import com.segar.backend.calendario.infrastructure.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;

    public List<EventoDTO> obtenerTodosLosEventos() {
        return eventoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> obtenerEventosPorMes(int mes, int anio) {
        return eventoRepository.findByMesAndAnio(mes, anio)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public EventoDTO obtenerEventoPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        return convertirADTO(evento);
    }

    //Eventos por mes y empresa
    public List<EventoDTO> obtenerEventosPorMesYEmpresa(int mes, int anio, Long empresaId) {
        return eventoRepository.findByEmpresaIdAndMesAndAnio(empresaId, mes, anio)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    //Eventos por mes y usuario
    public List<EventoDTO> obtenerEventosPorMesYUsuario(int mes, int anio, Long usuarioId) {
        return eventoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    //Eventos por usuario
    public List<EventoDTO> obtenerEventosPorUsuario(Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    //Próximos eventos por empresa
    public List<EventoDTO> obtenerProximosTresEventosPorEmpresa(Long empresaId) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        return eventoRepository.findTop3ProximosEventosByEmpresaId(empresaId, pageable)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    //Próximos eventos por usuario
    public List<EventoDTO> obtenerProximosTresEventosPorUsuario(Long usuarioId) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        return eventoRepository.findTop3ProximosEventosByUsuarioId(usuarioId, pageable)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public EventoDTO crearEvento(CrearEventoDTO crearEventoDTO) {
        Evento evento = Evento.builder()
                .titulo(crearEventoDTO.getTitulo())
                .descripcion(crearEventoDTO.getDescripcion())
                .fecha(crearEventoDTO.getFecha())
                .hora(crearEventoDTO.getHora())
                .tipo(crearEventoDTO.getTipo())
                .categoria(crearEventoDTO.getCategoria())
                .prioridad(crearEventoDTO.getPrioridad())
                .empresaId(crearEventoDTO.getEmpresaId())
                .tramiteId(crearEventoDTO.getTramiteId())
                .documentoId(crearEventoDTO.getDocumentoId())
                .build();

        Evento eventoGuardado = eventoRepository.save(evento);
        return convertirADTO(eventoGuardado);
    }

    public EventoDTO actualizarEvento(Long id, CrearEventoDTO actualizarEventoDTO) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setTitulo(actualizarEventoDTO.getTitulo());
        evento.setDescripcion(actualizarEventoDTO.getDescripcion());
        evento.setFecha(actualizarEventoDTO.getFecha());
        evento.setHora(actualizarEventoDTO.getHora());
        evento.setTipo(actualizarEventoDTO.getTipo());
        evento.setCategoria(actualizarEventoDTO.getCategoria());
        evento.setPrioridad(actualizarEventoDTO.getPrioridad());
        evento.setEmpresaId(actualizarEventoDTO.getEmpresaId());
        evento.setTramiteId(actualizarEventoDTO.getTramiteId());
        evento.setDocumentoId(actualizarEventoDTO.getDocumentoId());

        Evento eventoActualizado = eventoRepository.save(evento);
        return convertirADTO(eventoActualizado);
    }

    public void eliminarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado");
        }
        eventoRepository.deleteById(id);
    }

    public EventoDTO marcarComoCompletado(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setEstado(EstadoEvento.COMPLETADO);
        Evento eventoActualizado = eventoRepository.save(evento);
        return convertirADTO(eventoActualizado);
    }

    public List<EventoDTO> obtenerEventosPorEmpresa(Long empresaId) {
        return eventoRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private EventoDTO convertirADTO(Evento evento) {
        return EventoDTO.builder()
                .id(evento.getId())
                .titulo(evento.getTitulo())
                .descripcion(evento.getDescripcion())
                .fecha(evento.getFecha())
                .hora(evento.getHora())
                .tipo(evento.getTipo())
                .categoria(evento.getCategoria())
                .prioridad(evento.getPrioridad())
                .estado(evento.getEstado())
                .empresaId(evento.getEmpresaId())
                .tramiteId(evento.getTramiteId())
                .documentoId(evento.getDocumentoId())
                .fechaCreacion(evento.getFechaCreacion())
                .fechaActualizacion(evento.getFechaActualizacion())
                .build();
    }

    public List<EventoDTO> obtenerProximosTresEventos() {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        return eventoRepository.findTop3ProximosEventos(pageable)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

}
