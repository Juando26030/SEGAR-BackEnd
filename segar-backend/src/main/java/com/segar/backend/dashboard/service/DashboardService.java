package com.segar.backend.dashboard.service;

import com.segar.backend.dashboard.domain.dto.*;
import com.segar.backend.dashboard.infrastructure.DashboardQueryRepository;
import com.segar.backend.shared.domain.EstadoRegistro;
import com.segar.backend.tramites.infrastructure.RegistroSanitarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardQueryRepository queryRepository;
    private final RegistroSanitarioRepository registroSanitarioRepository;

    public DashboardService(DashboardQueryRepository queryRepository,
                            RegistroSanitarioRepository registroSanitarioRepository) {
        this.queryRepository = queryRepository;
        this.registroSanitarioRepository = registroSanitarioRepository;
    }

    // ==================== RESUMEN ====================

    public DashboardResumenDTO getResumen(int diasVencimientoVentana) {
        long totalTramites = queryRepository.totalTramites();
        long totalRegistros = queryRepository.totalRegistros();
        long registrosVigentes = queryRepository.registrosVigentes();
        long registrosPorVencer = queryRepository.registrosPorVencer(LocalDateTime.now().plusDays(diasVencimientoVentana));
        long registrosVencidos = totalRegistros - registrosVigentes;
        long reqPendientes = queryRepository.countRequerimientosPendientes();

        List<ConteoPorEstadoDTO> porEstado = tramitesPorEstado();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(totalRegistros)
                .registrosVigentes(registrosVigentes)
                .registrosPorVencer(registrosPorVencer)
                .registrosVencidos(registrosVencidos)
                .requerimientosPendientes(reqPendientes)
                .build();
    }

    public DashboardResumenDTO getResumenByEmpresa(Long empresaId, int diasVencimientoVentana) {
        long totalTramites = queryRepository.totalTramitesByEmpresa(empresaId);
        long totalRegistros = registroSanitarioRepository.countByEmpresaId(empresaId);
        long registrosVigentes = registroSanitarioRepository.countByEmpresaIdAndEstado(empresaId, EstadoRegistro.VIGENTE);
        long registrosPorVencer = registroSanitarioRepository.countByEmpresaIdAndVencimiento(
                empresaId,
                LocalDateTime.now().plusDays(diasVencimientoVentana)
        );
        long registrosVencidos = totalRegistros - registrosVigentes;

        List<ConteoPorEstadoDTO> porEstado = tramitesPorEstadoByEmpresa(empresaId);
        long reqPendientes = queryRepository.requerimientosPendientesByEmpresa(empresaId, 9999).size();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(totalRegistros)
                .registrosVigentes(registrosVigentes)
                .registrosPorVencer(registrosPorVencer)
                .registrosVencidos(registrosVencidos)
                .requerimientosPendientes(reqPendientes)
                .build();
    }

    public DashboardResumenDTO getResumenByUsuario(Long usuarioId) {
        long totalTramites = queryRepository.totalTramitesByUsuario(usuarioId);
        List<ConteoPorEstadoDTO> porEstado = tramitesPorEstadoByUsuario(usuarioId);
        long reqPendientes = queryRepository.requerimientosPendientesByUsuario(usuarioId, 9999).size();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(0)
                .registrosVigentes(0)
                .registrosPorVencer(0)
                .registrosVencidos(0)
                .requerimientosPendientes(reqPendientes)
                .build();
    }

    // ==================== TRÁMITES POR ESTADO ====================

    public List<ConteoPorEstadoDTO> tramitesPorEstado() {
        List<Object[]> rows = queryRepository.countTramitesByEstado();
        return rows.stream()
                .map(row -> new ConteoPorEstadoDTO(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<ConteoPorEstadoDTO> tramitesPorEstadoByEmpresa(Long empresaId) {
        List<Object[]> rows = queryRepository.countTramitesByEstadoAndEmpresa(empresaId);
        return rows.stream()
                .map(row -> new ConteoPorEstadoDTO(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();
    }

    public List<ConteoPorEstadoDTO> tramitesPorEstadoByUsuario(Long usuarioId) {
        List<Object[]> rows = queryRepository.countTramitesByEstadoAndUsuario(usuarioId);
        return rows.stream()
                .map(row -> new ConteoPorEstadoDTO(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();
    }

    // ==================== TRÁMITES POR MES ====================

    public List<SerieMesDTO> tramitesPorMes(int year) {
        List<Object[]> rows = queryRepository.countTramitesByMonth(year);
        return rows.stream()
                .map(row -> new SerieMesDTO(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public List<SerieMesDTO> tramitesPorMesByEmpresa(int year, Long empresaId) {
        List<Object[]> rows = queryRepository.countTramitesByMonth(year, empresaId);
        return rows.stream()
                .map(row -> new SerieMesDTO(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public List<SerieMesDTO> tramitesPorMesByUsuario(int year, Long usuarioId) {
        List<Object[]> rows = queryRepository.countTramitesByMonthByUsuario(year, usuarioId);
        return rows.stream()
                .map(row -> new SerieMesDTO(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    // ==================== TRÁMITES RECIENTES ====================

    public List<TramiteRecienteDTO> tramitesRecientes(int limit) {
        List<Object[]> rows = queryRepository.tramitesRecientes(limit);
        return mapearTramitesRecientes(rows);
    }

    public List<TramiteRecienteDTO> tramitesRecientesByEmpresa(Long empresaId, int limit) {
        List<Object[]> rows = queryRepository.tramitesRecientesByEmpresa(empresaId, limit);
        return mapearTramitesRecientes(rows);
    }

    public List<TramiteRecienteDTO> tramitesRecientesByUsuario(Long usuarioId, int limit) {
        List<Object[]> rows = queryRepository.tramitesRecientesByUsuario(usuarioId, limit);
        return mapearTramitesRecientes(rows);
    }

    private List<TramiteRecienteDTO> mapearTramitesRecientes(List<Object[]> rows) {
        List<TramiteRecienteDTO> out = new ArrayList<>();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            String radicado = (String) row[1];
            String producto = (String) row[2];
            String tipo = (String) row[3];
            String estado = String.valueOf(row[4]);
            LocalDateTime lastUpdate = (LocalDateTime) row[5];
            out.add(new TramiteRecienteDTO(id, radicado, producto, tipo, estado, lastUpdate));
        }
        return out;
    }

    // ==================== REQUERIMIENTOS PENDIENTES ====================

    public List<RequerimientoPendienteDTO> requerimientosPendientes(int limit) {
        List<Object[]> rows = queryRepository.requerimientosPendientesOrdenados(limit);
        return mapearRequerimientos(rows);
    }

    public List<RequerimientoPendienteDTO> requerimientosPendientesByEmpresa(Long empresaId, int limit) {
        List<Object[]> rows = queryRepository.requerimientosPendientesByEmpresa(empresaId, limit);
        return mapearRequerimientos(rows);
    }

    public List<RequerimientoPendienteDTO> requerimientosPendientesByUsuario(Long usuarioId, int limit) {
        List<Object[]> rows = queryRepository.requerimientosPendientesByUsuario(usuarioId, limit);
        return mapearRequerimientos(rows);
    }

    private List<RequerimientoPendienteDTO> mapearRequerimientos(List<Object[]> rows) {
        List<RequerimientoPendienteDTO> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            Long tramiteId = ((Number) row[1]).longValue();
            String number = (String) row[2];
            String title = (String) row[3];
            LocalDate deadline = (LocalDate) row[4];
            long diasRestantes = deadline != null ? today.until(deadline).getDays() : 0;
            out.add(new RequerimientoPendienteDTO(id, tramiteId, number, title, deadline, diasRestantes));
        }
        return out;
    }

    // ==================== REGISTROS POR AÑO ====================

    public long registrosPorAno(int year) {
        Long count = registroSanitarioRepository.countByYear(year);
        return count != null ? count : 0L;
    }

    public long registrosPorAnoByEmpresa(int year, Long empresaId) {
        List<?> registros = registroSanitarioRepository.findByEmpresaId(empresaId);
        return registros.stream()
                .filter(r -> {
                    try {
                        java.lang.reflect.Method m = r.getClass().getMethod("getFechaExpedicion");
                        Object val = m.invoke(r);
                        if (val instanceof LocalDateTime) {
                            return ((LocalDateTime) val).getYear() == year;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                    return false;
                }).count();
    }

    // ==================== DETALLE TRÁMITE ====================

    public TramiteDetalleDTO getTramiteDetalle(Long tramiteId) {
        try {
            Object[] tramiteData = queryRepository.getTramiteCompleto(tramiteId);

            List<Object[]> eventosData = queryRepository.getEventosByTramite(tramiteId);
            List<TramiteDetalleDTO.EventoTramiteDTO> eventos = eventosData.stream()
                    .map(row -> new TramiteDetalleDTO.EventoTramiteDTO(
                            (String) row[0],
                            (String) row[1],
                            (LocalDate) row[2],
                            (Boolean) row[3],
                            (Boolean) row[4],
                            ((Number) row[5]).intValue()
                    )).toList();

            List<Object[]> reqData = queryRepository.getRequerimientosByTramite(tramiteId);
            LocalDate today = LocalDate.now();
            List<TramiteDetalleDTO.RequerimientoInfoDTO> requerimientos = reqData.stream()
                    .map(row -> {
                        LocalDate deadline = (LocalDate) row[3];
                        long diasRestantes = deadline != null ? today.until(deadline).getDays() : 0;
                        return new TramiteDetalleDTO.RequerimientoInfoDTO(
                                (String) row[0],
                                (String) row[1],
                                (String) row[2],
                                deadline,
                                String.valueOf(row[4]),
                                diasRestantes
                        );
                    }).toList();

            List<Object[]> notifData = queryRepository.getNotificacionesByTramite(tramiteId, 10);
            List<TramiteDetalleDTO.NotificacionInfoDTO> notificaciones = notifData.stream()
                    .map(row -> new TramiteDetalleDTO.NotificacionInfoDTO(
                            String.valueOf(row[0]),
                            (String) row[1],
                            (String) row[2],
                            (LocalDateTime) row[3],
                            (Boolean) row[4]
                    )).toList();

            List<Object[]> historialData = queryRepository.getHistorialByTramite(tramiteId);
            List<TramiteDetalleDTO.HistorialTramiteDTO> historial = historialData.stream()
                    .map(row -> new TramiteDetalleDTO.HistorialTramiteDTO(
                            (LocalDateTime) row[0],
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (String) row[4]
                    )).toList();

            LocalDate submissionDate = (LocalDate) tramiteData[2];
            long diasTranscurridos = submissionDate.until(LocalDate.now()).getDays();
            int eventosCompletados = (int) eventos.stream().mapToInt(e -> e.isCompleted() ? 1 : 0).sum();
            int reqPendientes = (int) requerimientos.stream().mapToInt(r -> r.getStatus().equals("PENDIENTE") ? 1 : 0).sum();
            int notifNoLeidas = (int) notificaciones.stream().mapToInt(n -> !n.isRead() ? 1 : 0).sum();
            double progreso = eventos.isEmpty() ? 0 : (eventosCompletados * 100.0) / eventos.size();

            return TramiteDetalleDTO.builder()
                    .id(((Number) tramiteData[0]).longValue())
                    .radicadoNumber((String) tramiteData[1])
                    .submissionDate(submissionDate)
                    .procedureType((String) tramiteData[3])
                    .productName((String) tramiteData[4])
                    .currentStatus(String.valueOf(tramiteData[5]))
                    .lastUpdate((LocalDateTime) tramiteData[6])
                    .eventos(eventos)
                    .requerimientos(requerimientos)
                    .notificaciones(notificaciones)
                    .historial(historial)
                    .estadisticas(new TramiteDetalleDTO.EstadisticasTramiteDTO(
                            diasTranscurridos,
                            eventos.size(),
                            eventosCompletados,
                            reqPendientes,
                            notifNoLeidas,
                            progreso
                    ))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Trámite no encontrado con ID: " + tramiteId, e);
        }
    }

    // ==================== BÚSQUEDA GLOBAL ====================

    public BusquedaGlobalDTO busquedaGlobal(String query, int limitTramites, int limitRegistros) {
        String queryTrimmed = (query != null) ? query.trim() : "";
        boolean esConsultaVacia = queryTrimmed.isEmpty();

        List<Object[]> tramitesData;
        List<Object[]> registrosData;
        int totalTramites;
        int totalRegistros;

        if (esConsultaVacia) {
            tramitesData = queryRepository.tramitesRecientes(limitTramites);
            registrosData = queryRepository.registrosSanitariosRecientes(limitRegistros);
            totalTramites = (int) queryRepository.totalTramites();
            totalRegistros = (int) queryRepository.totalRegistros();
        } else {
            tramitesData = queryRepository.buscarTramites(queryTrimmed, limitTramites);
            registrosData = queryRepository.buscarRegistrosSanitarios(queryTrimmed, limitRegistros);
            totalTramites = queryRepository.countTramitesBusqueda(queryTrimmed);
            totalRegistros = queryRepository.countRegistrosBusqueda(queryTrimmed);
        }

        List<BusquedaGlobalDTO.ResultadoTramiteDTO> tramites = mapearResultadosTramites(tramitesData, esConsultaVacia);
        List<BusquedaGlobalDTO.ResultadoRegistroDTO> registros = mapearResultadosRegistros(registrosData);

        return new BusquedaGlobalDTO(tramites, registros, totalTramites, totalRegistros);
    }

    public BusquedaGlobalDTO busquedaGlobalByEmpresa(String query, Long empresaId, int limitTramites, int limitRegistros) {
        String queryTrimmed = (query != null) ? query.trim() : "";
        boolean esConsultaVacia = queryTrimmed.isEmpty();

        List<Object[]> tramitesData;
        List<Object[]> registrosData;
        int totalTramites;
        int totalRegistros;

        if (esConsultaVacia) {
            tramitesData = queryRepository.tramitesRecientesByEmpresa(empresaId, limitTramites);
            registrosData = queryRepository.registrosSanitariosRecientesByEmpresa(empresaId, limitRegistros);
            totalTramites = (int) queryRepository.totalTramitesByEmpresa(empresaId);
            totalRegistros = Math.toIntExact(registroSanitarioRepository.countByEmpresaId(empresaId));
        } else {
            tramitesData = queryRepository.buscarTramitesByEmpresa(queryTrimmed, empresaId, limitTramites);
            registrosData = queryRepository.buscarRegistrosSanitariosByEmpresa(queryTrimmed, empresaId, limitRegistros);
            totalTramites = queryRepository.countTramitesBusquedaByEmpresa(queryTrimmed, empresaId);
            totalRegistros = queryRepository.countRegistrosBusquedaByEmpresa(queryTrimmed, empresaId);
        }

        List<BusquedaGlobalDTO.ResultadoTramiteDTO> tramites = mapearResultadosTramites(tramitesData, esConsultaVacia);
        List<BusquedaGlobalDTO.ResultadoRegistroDTO> registros = mapearResultadosRegistros(registrosData);

        return new BusquedaGlobalDTO(tramites, registros, totalTramites, totalRegistros);
    }

    public BusquedaGlobalDTO busquedaGlobalByUsuario(String query, Long usuarioId, int limitTramites) {
        String queryTrimmed = (query != null) ? query.trim() : "";
        boolean esConsultaVacia = queryTrimmed.isEmpty();

        List<Object[]> tramitesData;
        int totalTramites;

        if (esConsultaVacia) {
            tramitesData = queryRepository.tramitesRecientesByUsuario(usuarioId, limitTramites);
            totalTramites = (int) queryRepository.totalTramitesByUsuario(usuarioId);
        } else {
            tramitesData = queryRepository.buscarTramitesByUsuario(queryTrimmed, usuarioId, limitTramites);
            totalTramites = queryRepository.countTramitesBusquedaByUsuario(queryTrimmed, usuarioId);
        }

        List<BusquedaGlobalDTO.ResultadoTramiteDTO> tramites = mapearResultadosTramites(tramitesData, esConsultaVacia);

        return new BusquedaGlobalDTO(tramites, List.of(), totalTramites, 0);
    }

    private List<BusquedaGlobalDTO.ResultadoTramiteDTO> mapearResultadosTramites(List<Object[]> tramitesData, boolean esConsultaVacia) {
        if (esConsultaVacia) {
            // tramitesRecientes: 6 campos (sin submissionDate)
            return tramitesData.stream()
                    .map(row -> new BusquedaGlobalDTO.ResultadoTramiteDTO(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            String.valueOf(row[4]),
                            null,
                            (LocalDateTime) row[5]
                    )).toList();
        } else {
            // buscarTramites: 7 campos (con submissionDate)
            return tramitesData.stream()
                    .map(row -> new BusquedaGlobalDTO.ResultadoTramiteDTO(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            String.valueOf(row[4]),
                            (LocalDate) row[5],
                            (LocalDateTime) row[6]
                    )).toList();
        }
    }

    private List<BusquedaGlobalDTO.ResultadoRegistroDTO> mapearResultadosRegistros(List<Object[]> registrosData) {
        return registrosData.stream()
                .map(row -> new BusquedaGlobalDTO.ResultadoRegistroDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        String.valueOf(row[3]),
                        (LocalDateTime) row[4],
                        (LocalDateTime) row[5]
                )).toList();
    }

}
