package com.segar.backend.dashboard.api;

import com.segar.backend.dashboard.domain.dto.*;
import com.segar.backend.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen(
            @RequestParam(name = "diasVencimiento", required = false, defaultValue = "30") int diasVencimiento
    ) {
        return ResponseEntity.ok(dashboardService.getResumen(diasVencimiento));
    }

    @GetMapping("/resumen/empresa/{empresaId}")
    public ResponseEntity<DashboardResumenDTO> getResumenEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "30") int diasVencimiento) {
        return ResponseEntity.ok(dashboardService.getResumenByEmpresa(empresaId, diasVencimiento));
    }

    @GetMapping("/resumen/usuario/{usuarioId}")
    public ResponseEntity<DashboardResumenDTO> getResumenUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "30") int diasVencimiento) {
        return ResponseEntity.ok(dashboardService.getResumenByUsuario(usuarioId, diasVencimiento));
    }


    @GetMapping("/tramites/por-estado")
    public ResponseEntity<List<ConteoPorEstadoDTO>> tramitesPorEstado() {
        return ResponseEntity.ok(dashboardService.tramitesPorEstado());
    }

    @GetMapping("/tramites/por-mes")
    public ResponseEntity<List<SerieMesDTO>> tramitesPorMes(
            @RequestParam(name = "year", required = false) Integer year
    ) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.tramitesPorMes(y));
    }
    @GetMapping("/tramites/por-mes/empresa/{empresaId}")
    public ResponseEntity<List<SerieMesDTO>> tramitesPorMesEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(name = "year", required = false) Integer year) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.tramitesPorMesByEmpresa(y, empresaId));
    }

    @GetMapping("/tramites/por-mes/usuario/{usuarioId}")
    public ResponseEntity<List<SerieMesDTO>> tramitesPorMesUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(name = "year", required = false) Integer year) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.tramitesPorMesByUsuario(y, usuarioId));
    }


    @GetMapping("/requerimientos/pendientes")
    public ResponseEntity<List<RequerimientoPendienteDTO>> requerimientosPendientes(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(dashboardService.requerimientosPendientes(limit));
    }

    @GetMapping("/registros/por-ano")
    public ResponseEntity<Long> registrosPorAno(
            @RequestParam(name = "year", required = true) int year
    ) {
        return ResponseEntity.ok(dashboardService.registrosPorAno(year));
    }


    @GetMapping("/tramites/recientes")
    public ResponseEntity<List<TramiteRecienteDTO>> tramitesRecientes(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(dashboardService.tramitesRecientes(limit));
    }

    @GetMapping("/tramite/{id}")
    public ResponseEntity<TramiteDetalleDTO> getTramiteDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getTramiteDetalle(id));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<BusquedaGlobalDTO> busquedaGlobal(
            @RequestParam(name = "q", required = true) String query,
            @RequestParam(name = "limitTramites", required = false, defaultValue = "10") int limitTramites,
            @RequestParam(name = "limitRegistros", required = false, defaultValue = "10") int limitRegistros
    ) {
        return ResponseEntity.ok(dashboardService.busquedaGlobal(query, limitTramites, limitRegistros));
    }

}
