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

    public List<ConteoPorEstadoDTO> tramitesPorEstado() {
        List<Object[]> rows = queryRepository.countTramitesByEstado();
        List<ConteoPorEstadoDTO> out = new ArrayList<>();
        for (Object[] row : rows) {
            String estado = String.valueOf(row[0]);
            long cantidad = ((Number) row[1]).longValue();
            out.add(new ConteoPorEstadoDTO(estado, cantidad));
        }
        return out;
    }

    public List<SerieMesDTO> tramitesPorMes(int year) {
        List<Object[]> rows = queryRepository.countTramitesByMonth(year);
        List<SerieMesDTO> out = new ArrayList<>();
        for (Object[] row : rows) {
            int mes = ((Number) row[0]).intValue();
            long cantidad = ((Number) row[1]).longValue();
            out.add(new SerieMesDTO(mes, cantidad));
        }
        return out;
    }

    public List<RequerimientoPendienteDTO> requerimientosPendientes(int limit) {
        List<Object[]> rows = queryRepository.requerimientosPendientesOrdenados(limit);
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

    public long registrosPorAno(int year) {
        Long count = registroSanitarioRepository.countByYear(year);
        return count != null ? count : 0L;
    }

    public List<TramiteRecienteDTO> tramitesRecientes(int limit) {
        List<Object[]> rows = queryRepository.tramitesRecientes(limit);
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

    public TramiteDetalleDTO getTramiteDetalle(Long tramiteId) {
        try {
            // Información básica del trámite
            Object[] tramiteData = queryRepository.getTramiteCompleto(tramiteId);

            // Eventos
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

            // Requerimientos
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

            // Notificaciones
            List<Object[]> notifData = queryRepository.getNotificacionesByTramite(tramiteId, 10);
            List<TramiteDetalleDTO.NotificacionInfoDTO> notificaciones = notifData.stream()
                    .map(row -> new TramiteDetalleDTO.NotificacionInfoDTO(
                            String.valueOf(row[0]),
                            (String) row[1],
                            (String) row[2],
                            (LocalDateTime) row[3],
                            (Boolean) row[4]
                    )).toList();

            // Historial
            List<Object[]> historialData = queryRepository.getHistorialByTramite(tramiteId);
            List<TramiteDetalleDTO.HistorialTramiteDTO> historial = historialData.stream()
                    .map(row -> new TramiteDetalleDTO.HistorialTramiteDTO(
                            (LocalDateTime) row[0],
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (String) row[4]
                    )).toList();

            // Calcular estadísticas
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

    //Busqueda Global

    public BusquedaGlobalDTO busquedaGlobal(String query, int limitTramites, int limitRegistros) {
        String queryTrimmed = (query != null) ? query.trim() : "";

        List<Object[]> tramitesData;
        List<Object[]> registrosData;
        int totalTramites;
        int totalRegistros;
        boolean esConsultaVacia = queryTrimmed.isEmpty();

        if (esConsultaVacia) {
            // Si la query está vacía, traer los primeros registros ordenados
            tramitesData = queryRepository.tramitesRecientes(limitTramites);
            registrosData = queryRepository.registrosSanitariosRecientes(limitRegistros);
            totalTramites = (int) queryRepository.totalTramites();
            totalRegistros = (int) queryRepository.totalRegistros();
        } else {
            // Búsqueda normal con filtros
            tramitesData = queryRepository.buscarTramites(queryTrimmed, limitTramites);
            registrosData = queryRepository.buscarRegistrosSanitarios(queryTrimmed, limitRegistros);
            totalTramites = queryRepository.countTramitesBusqueda(queryTrimmed);
            totalRegistros = queryRepository.countRegistrosBusqueda(queryTrimmed);
        }

        // Mapear trámites con lógica diferente según el origen
        List<BusquedaGlobalDTO.ResultadoTramiteDTO> tramites;

        if (esConsultaVacia) {
            // Mapeo para tramitesRecientes (6 campos, sin submissionDate)
            tramites = tramitesData.stream()
                    .map(row -> new BusquedaGlobalDTO.ResultadoTramiteDTO(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            String.valueOf(row[4]),
                            null,                    // submissionDate no disponible
                            (LocalDateTime) row[5]   // lastUpdate en posición 5
                    )).toList();
        } else {
            // Mapeo para buscarTramites (7 campos, con submissionDate)
            tramites = tramitesData.stream()
                    .map(row -> new BusquedaGlobalDTO.ResultadoTramiteDTO(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            String.valueOf(row[4]),
                            (LocalDate) row[5],      // submissionDate en posición 5
                            (LocalDateTime) row[6]   // lastUpdate en posición 6
                    )).toList();
        }

        // Mapear registros sanitarios (sin cambios)
        List<BusquedaGlobalDTO.ResultadoRegistroDTO> registros = registrosData.stream()
                .map(row -> new BusquedaGlobalDTO.ResultadoRegistroDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        String.valueOf(row[3]),
                        (LocalDateTime) row[4],
                        (LocalDateTime) row[5]
                )).toList();

        return new BusquedaGlobalDTO(tramites, registros, totalTramites, totalRegistros);
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

        List<Object[]> porEstadoData = queryRepository.countTramitesByEstadoAndEmpresa(empresaId);
        List<ConteoPorEstadoDTO> porEstado = porEstadoData.stream()
                .map(row -> new ConteoPorEstadoDTO(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(totalRegistros)
                .registrosVigentes(registrosVigentes)
                .registrosPorVencer(registrosPorVencer)
                .registrosVencidos(registrosVencidos)
                .build();
    }

    public DashboardResumenDTO getResumenByUsuario(Long usuarioId, int diasVencimientoVentana) {
        long totalTramites = queryRepository.totalTramitesByUsuario(usuarioId);

        List<Object[]> porEstadoData = queryRepository.countTramitesByEstadoAndUsuario(usuarioId);
        List<ConteoPorEstadoDTO> porEstado = porEstadoData.stream()
                .map(row -> new ConteoPorEstadoDTO(String.valueOf(row[0]), ((Number) row[1]).longValue()))
                .toList();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(0)
                .registrosVigentes(0)
                .registrosPorVencer(0)
                .registrosVencidos(0)
                .build();
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




}
