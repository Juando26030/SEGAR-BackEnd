package com.segar.backend.dashboard.service;

import com.segar.backend.dashboard.domain.dto.*;
import com.segar.backend.dashboard.infrastructure.DashboardQueryRepository;
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
        long reqPendientes = queryRepository.countRequerimientosPendientes();

        List<ConteoPorEstadoDTO> porEstado = tramitesPorEstado();

        return DashboardResumenDTO.builder()
                .totalTramites(totalTramites)
                .tramitesPorEstado(porEstado)
                .totalRegistros(totalRegistros)
                .registrosVigentes(registrosVigentes)
                .registrosPorVencer(registrosPorVencer)
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
}
