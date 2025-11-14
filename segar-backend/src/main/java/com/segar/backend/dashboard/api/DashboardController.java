package com.segar.backend.dashboard.api;

import com.segar.backend.dashboard.domain.dto.*;
import com.segar.backend.dashboard.service.DashboardService;
import com.segar.backend.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Valida que el empresaId del request coincida con el del usuario autenticado
     */
    private void validateTenantAccess(Long empresaIdRequest) {
        Long empresaIdUsuario = authenticatedUserService.getCurrentUserEmpresaId();
        if (!empresaIdRequest.equals(empresaIdUsuario)) {
            throw new AccessDeniedException("No tienes permiso para acceder a datos de otra empresa");
        }
    }

    // ==================== RESUMEN ====================

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen(
            @RequestParam(name = "diasVencimiento", required = false, defaultValue = "30") int diasVencimiento
    ) {
        // Usar automáticamente la empresa del usuario autenticado
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        return ResponseEntity.ok(dashboardService.getResumenByEmpresa(empresaId, diasVencimiento));
    }

    @GetMapping("/resumen/empresa/{empresaId}")
    public ResponseEntity<DashboardResumenDTO> getResumenEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "30") int diasVencimiento) {
        validateTenantAccess(empresaId);
        return ResponseEntity.ok(dashboardService.getResumenByEmpresa(empresaId, diasVencimiento));
    }

    @GetMapping("/resumen/usuario/{usuarioId}")
    public ResponseEntity<DashboardResumenDTO> getResumenUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(dashboardService.getResumenByUsuario(usuarioId));
    }

    // ==================== TRÁMITES POR ESTADO ====================

    @GetMapping("/tramites/por-estado")
    public ResponseEntity<List<ConteoPorEstadoDTO>> tramitesPorEstado() {
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        return ResponseEntity.ok(dashboardService.tramitesPorEstadoByEmpresa(empresaId));
    }

    @GetMapping("/tramites/por-estado/empresa/{empresaId}")
    public ResponseEntity<List<ConteoPorEstadoDTO>> tramitesPorEstadoEmpresa(@PathVariable Long empresaId) {
        validateTenantAccess(empresaId);
        return ResponseEntity.ok(dashboardService.tramitesPorEstadoByEmpresa(empresaId));
    }

    @GetMapping("/tramites/por-estado/usuario/{usuarioId}")
    public ResponseEntity<List<ConteoPorEstadoDTO>> tramitesPorEstadoUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(dashboardService.tramitesPorEstadoByUsuario(usuarioId));
    }

    // ==================== TRÁMITES POR MES ====================

    @GetMapping("/tramites/por-mes")
    public ResponseEntity<List<SerieMesDTO>> tramitesPorMes(
            @RequestParam(name = "year", required = false) Integer year
    ) {
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.tramitesPorMesByEmpresa(y, empresaId));
    }

    @GetMapping("/tramites/por-mes/empresa/{empresaId}")
    public ResponseEntity<List<SerieMesDTO>> tramitesPorMesEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(name = "year", required = false) Integer year) {
        validateTenantAccess(empresaId);
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

    // ==================== TRÁMITES RECIENTES ====================

    @GetMapping("/tramites/recientes")
    public ResponseEntity<List<TramiteRecienteDTO>> tramitesRecientes(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit
    ) {
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        return ResponseEntity.ok(dashboardService.tramitesRecientesByEmpresa(empresaId, limit));
    }

    @GetMapping("/tramites/recientes/empresa/{empresaId}")
    public ResponseEntity<List<TramiteRecienteDTO>> tramitesRecientesEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit) {
        validateTenantAccess(empresaId);
        return ResponseEntity.ok(dashboardService.tramitesRecientesByEmpresa(empresaId, limit));
    }

    @GetMapping("/tramites/recientes/usuario/{usuarioId}")
    public ResponseEntity<List<TramiteRecienteDTO>> tramitesRecientesUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.tramitesRecientesByUsuario(usuarioId, limit));
    }

    // ==================== REQUERIMIENTOS PENDIENTES ====================

    @GetMapping("/requerimientos/pendientes")
    public ResponseEntity<List<RequerimientoPendienteDTO>> requerimientosPendientes(
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(dashboardService.requerimientosPendientes(limit));
    }

    @GetMapping("/requerimientos/pendientes/empresa/{empresaId}")
    public ResponseEntity<List<RequerimientoPendienteDTO>> requerimientosPendientesEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.requerimientosPendientesByEmpresa(empresaId, limit));
    }

    @GetMapping("/requerimientos/pendientes/usuario/{usuarioId}")
    public ResponseEntity<List<RequerimientoPendienteDTO>> requerimientosPendientesUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.requerimientosPendientesByUsuario(usuarioId, limit));
    }

    // ==================== REGISTROS POR AÑO ====================

    @GetMapping("/registros/por-ano")
    public ResponseEntity<Long> registrosPorAno(@RequestParam int year) {
        return ResponseEntity.ok(dashboardService.registrosPorAno(year));
    }

    @GetMapping("/registros/por-ano/empresa/{empresaId}")
    public ResponseEntity<Long> registrosPorAnoEmpresa(
            @PathVariable Long empresaId,
            @RequestParam int year) {
        return ResponseEntity.ok(dashboardService.registrosPorAnoByEmpresa(year, empresaId));
    }

    // ==================== DETALLE TRÁMITE ====================

    @GetMapping("/tramite/{id}")
    public ResponseEntity<TramiteDetalleDTO> getTramiteDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getTramiteDetalle(id));
    }

    // ==================== BÚSQUEDA GLOBAL ====================

    @GetMapping("/busqueda")
    public ResponseEntity<BusquedaGlobalDTO> busquedaGlobal(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "limitTramites", required = false, defaultValue = "10") int limitTramites,
            @RequestParam(name = "limitRegistros", required = false, defaultValue = "10") int limitRegistros
    ) {
        return ResponseEntity.ok(dashboardService.busquedaGlobal(query, limitTramites, limitRegistros));
    }

    @GetMapping("/busqueda/empresa/{empresaId}")
    public ResponseEntity<BusquedaGlobalDTO> busquedaGlobalEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(name = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "limitTramites", required = false, defaultValue = "10") int limitTramites,
            @RequestParam(name = "limitRegistros", required = false, defaultValue = "10") int limitRegistros
    ) {
        return ResponseEntity.ok(dashboardService.busquedaGlobalByEmpresa(query, empresaId, limitTramites, limitRegistros));
    }

    @GetMapping("/busqueda/usuario/{usuarioId}")
    public ResponseEntity<BusquedaGlobalDTO> busquedaGlobalUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(name = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "limitTramites", required = false, defaultValue = "10") int limitTramites
    ) {
        return ResponseEntity.ok(dashboardService.busquedaGlobalByUsuario(query, usuarioId, limitTramites));
    }
}
