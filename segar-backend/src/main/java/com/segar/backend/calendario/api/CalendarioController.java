package com.segar.backend.calendario.api;

import com.segar.backend.calendario.api.dto.CrearEventoDTO;
import com.segar.backend.calendario.api.dto.EstadisticasCalendarioDTO;
import com.segar.backend.calendario.api.dto.EventoDTO;
import com.segar.backend.calendario.domain.CategoriaEvento;
import com.segar.backend.calendario.domain.EstadoEvento;
import com.segar.backend.calendario.domain.PrioridadEvento;
import com.segar.backend.calendario.domain.TipoEvento;
import com.segar.backend.calendario.service.CalendarioService;
import com.segar.backend.calendario.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CalendarioController {

    private final EventoService eventoService;
    private final CalendarioService calendarioService;

    @GetMapping("/eventos")
    public ResponseEntity<List<EventoDTO>> obtenerTodosLosEventos() {
        List<EventoDTO> eventos = eventoService.obtenerTodosLosEventos();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/eventos/{mes}/{anio}")
    public ResponseEntity<List<EventoDTO>> obtenerEventosPorMes(
            @PathVariable int mes,
            @PathVariable int anio) {
        List<EventoDTO> eventos = eventoService.obtenerEventosPorMes(mes, anio);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/eventos/{id}")
    public ResponseEntity<EventoDTO> obtenerEventoPorId(@PathVariable Long id) {
        EventoDTO evento = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/eventos")
    public ResponseEntity<EventoDTO> crearEvento(@Valid @RequestBody CrearEventoDTO crearEventoDTO) {
        EventoDTO eventoCreado = eventoService.crearEvento(crearEventoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoCreado);
    }

    @PutMapping("/eventos/{id}")
    public ResponseEntity<EventoDTO> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody CrearEventoDTO actualizarEventoDTO) {
        EventoDTO eventoActualizado = eventoService.actualizarEvento(id, actualizarEventoDTO);
        return ResponseEntity.ok(eventoActualizado);
    }

    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/eventos/{id}/completar")
    public ResponseEntity<EventoDTO> marcarComoCompletado(@PathVariable Long id) {
        EventoDTO eventoCompletado = eventoService.marcarComoCompletado(id);
        return ResponseEntity.ok(eventoCompletado);
    }

    @GetMapping("/eventos/empresa/{empresaId}")
    public ResponseEntity<List<EventoDTO>> obtenerEventosPorEmpresa(@PathVariable Long empresaId) {
        List<EventoDTO> eventos = eventoService.obtenerEventosPorEmpresa(empresaId);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasCalendarioDTO> obtenerEstadisticas() {
        EstadisticasCalendarioDTO estadisticas = calendarioService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoEvento>> obtenerTiposEvento() {
        List<TipoEvento> tipos = calendarioService.obtenerTiposEvento();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaEvento>> obtenerCategoriasEvento() {
        List<CategoriaEvento> categorias = calendarioService.obtenerCategoriasEvento();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/prioridades")
    public ResponseEntity<List<PrioridadEvento>> obtenerPrioridadesEvento() {
        List<PrioridadEvento> prioridades = calendarioService.obtenerPrioridadesEvento();
        return ResponseEntity.ok(prioridades);
    }

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoEvento>> obtenerEstadosEvento() {
        List<EstadoEvento> estados = calendarioService.obtenerEstadosEvento();
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/eventos/proximos")
    public ResponseEntity<List<EventoDTO>> obtenerProximosEventos() {
        List<EventoDTO> proximosEventos = eventoService.obtenerProximosTresEventos();
        return ResponseEntity.ok(proximosEventos);
    }

}
