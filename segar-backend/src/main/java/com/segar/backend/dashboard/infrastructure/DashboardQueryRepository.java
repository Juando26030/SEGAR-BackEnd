package com.segar.backend.dashboard.infrastructure;

import com.segar.backend.shared.domain.EstadoRegistro;
import com.segar.backend.shared.domain.EstadoRequerimiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DashboardQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> countTramitesByEstado() {
        return em.createQuery(
                "select t.currentStatus, count(t) from Tramite t group by t.currentStatus",
                Object[].class
        ).getResultList();
    }

    public List<Object[]> countTramitesByMonth(int year) {
        return em.createQuery(
                "select month(t.submissionDate), count(t) from Tramite t where year(t.submissionDate) = :year group by month(t.submissionDate) order by month(t.submissionDate)",
                Object[].class
        ).setParameter("year", year).getResultList();
    }

    public long totalTramites() {
        return em.createQuery("select count(t) from Tramite t", Long.class).getSingleResult();
    }

    public long totalRegistros() {
        return em.createQuery("select count(r) from RegistroSanitario r", Long.class).getSingleResult();
    }

    public long registrosVigentes() {
        return em.createQuery("select count(r) from RegistroSanitario r where r.estado = :estado", Long.class)
                .setParameter("estado", EstadoRegistro.VIGENTE)
                .getSingleResult();
    }

    public long registrosPorVencer(LocalDateTime limite) {
        return em.createQuery("select count(r) from RegistroSanitario r where r.estado = :estado and r.fechaVencimiento <= :limite", Long.class)
                .setParameter("estado", EstadoRegistro.VIGENTE)
                .setParameter("limite", limite)
                .getSingleResult();
    }

    public List<Object[]> requerimientosPendientesOrdenados(int limit) {
        return em.createQuery(
                "select r.id, r.tramite.id, r.number, r.title, r.deadline from Requerimiento r where r.status = :status order by r.deadline asc",
                Object[].class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countRequerimientosPendientes() {
        return em.createQuery(
                "select count(r) from Requerimiento r where r.status = :status",
                Long.class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .getSingleResult();
    }
}
