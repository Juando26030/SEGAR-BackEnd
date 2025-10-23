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

    // ==================== RESUMEN: TOTALES ====================

    public long totalTramites() {
        return em.createQuery("select count(t) from Tramite t", Long.class).getSingleResult();
    }

    public long totalTramitesByEmpresa(Long empresaId) {
        return em.createQuery(
                "select count(t) from Tramite t where t.product.empresaId = :empresaId",
                Long.class
        ).setParameter("empresaId", empresaId).getSingleResult();
    }

    public long totalTramitesByUsuario(Long usuarioId) {
        return em.createQuery(
                "select count(t) from Tramite t where t.usuario.id = :usuarioId",
                Long.class
        ).setParameter("usuarioId", usuarioId).getSingleResult();
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

    public long countRequerimientosPendientes() {
        return em.createQuery(
                "select count(r) from Requerimiento r where r.status = :status",
                Long.class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .getSingleResult();
    }

    // ==================== TRÁMITES POR ESTADO ====================

    public List<Object[]> countTramitesByEstado() {
        return em.createQuery(
                "select t.currentStatus, count(t) from Tramite t group by t.currentStatus",
                Object[].class
        ).getResultList();
    }

    public List<Object[]> countTramitesByEstadoAndEmpresa(Long empresaId) {
        return em.createQuery(
                "select t.currentStatus, count(t) from Tramite t " +
                        "where t.product.empresaId = :empresaId " +
                        "group by t.currentStatus",
                Object[].class
        ).setParameter("empresaId", empresaId).getResultList();
    }

    public List<Object[]> countTramitesByEstadoAndUsuario(Long usuarioId) {
        return em.createQuery(
                "select t.currentStatus, count(t) from Tramite t " +
                        "where t.usuario.id = :usuarioId " +
                        "group by t.currentStatus",
                Object[].class
        ).setParameter("usuarioId", usuarioId).getResultList();
    }

    // ==================== TRÁMITES POR MES ====================

    public List<Object[]> countTramitesByMonth(int year) {
        return em.createQuery(
                "select month(t.submissionDate), count(t) from Tramite t where year(t.submissionDate) = :year group by month(t.submissionDate) order by month(t.submissionDate)",
                Object[].class
        ).setParameter("year", year).getResultList();
    }

    public List<Object[]> countTramitesByMonth(int year, Long empresaId) {
        return em.createQuery(
                "select month(t.submissionDate), count(t) from Tramite t " +
                        "where year(t.submissionDate) = :year and t.product.empresaId = :empresaId " +
                        "group by month(t.submissionDate) order by month(t.submissionDate)",
                Object[].class
        ).setParameter("year", year)
                .setParameter("empresaId", empresaId)
                .getResultList();
    }

    public List<Object[]> countTramitesByMonthByUsuario(int year, Long usuarioId) {
        return em.createQuery(
                "select month(t.submissionDate), count(t) from Tramite t " +
                        "where year(t.submissionDate) = :year and t.usuario.id = :usuarioId " +
                        "group by month(t.submissionDate) order by month(t.submissionDate)",
                Object[].class
        ).setParameter("year", year)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    // ==================== TRÁMITES RECIENTES ====================

    public List<Object[]> tramitesRecientes(int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), t.procedureType, t.currentStatus, t.lastUpdate " +
                        "from Tramite t left join t.product p " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setMaxResults(limit).getResultList();
    }

    public List<Object[]> tramitesRecientesByEmpresa(Long empresaId, int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), " +
                        "t.procedureType, t.currentStatus, t.lastUpdate " +
                        "from Tramite t left join t.product p " +
                        "where p.empresaId = :empresaId " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setParameter("empresaId", empresaId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> tramitesRecientesByUsuario(Long usuarioId, int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), " +
                        "t.procedureType, t.currentStatus, t.lastUpdate " +
                        "from Tramite t left join t.product p " +
                        "where t.usuario.id = :usuarioId " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setParameter("usuarioId", usuarioId)
                .setMaxResults(limit)
                .getResultList();
    }

    // ==================== REQUERIMIENTOS PENDIENTES ====================

    public List<Object[]> requerimientosPendientesOrdenados(int limit) {
        return em.createQuery(
                "select r.id, r.tramite.id, r.number, r.title, r.deadline from Requerimiento r where r.status = :status order by r.deadline asc",
                Object[].class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> requerimientosPendientesByEmpresa(Long empresaId, int limit) {
        return em.createQuery(
                "select r.id, r.tramite.id, r.number, r.title, r.deadline " +
                        "from Requerimiento r left join r.tramite t left join t.product p " +
                        "where r.status = :status and p.empresaId = :empresaId order by r.deadline asc",
                Object[].class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .setParameter("empresaId", empresaId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> requerimientosPendientesByUsuario(Long usuarioId, int limit) {
        return em.createQuery(
                "select r.id, r.tramite.id, r.number, r.title, r.deadline " +
                        "from Requerimiento r left join r.tramite t " +
                        "where r.status = :status and t.usuario.id = :usuarioId order by r.deadline asc",
                Object[].class
        ).setParameter("status", EstadoRequerimiento.PENDIENTE)
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(limit)
                .getResultList();
    }

    // ==================== BÚSQUEDA TRÁMITES ====================

    public List<Object[]> buscarTramites(String query, int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), t.procedureType, t.currentStatus, t.submissionDate, t.lastUpdate " +
                        "from Tramite t left join t.product p where " +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%')) " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setParameter("query", query)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> buscarTramitesByEmpresa(String query, Long empresaId, int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), t.procedureType, t.currentStatus, t.submissionDate, t.lastUpdate " +
                        "from Tramite t left join t.product p where (" +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%')) ) and p.empresaId = :empresaId " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setParameter("query", query)
                .setParameter("empresaId", empresaId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> buscarTramitesByUsuario(String query, Long usuarioId, int limit) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, coalesce(p.nombre, 'Sin producto'), t.procedureType, t.currentStatus, t.submissionDate, t.lastUpdate " +
                        "from Tramite t left join t.product p where (" +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%')) ) and t.usuario.id = :usuarioId " +
                        "order by t.lastUpdate desc",
                Object[].class
        ).setParameter("query", query)
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(limit)
                .getResultList();
    }

    public int countTramitesBusqueda(String query) {
        return ((Number) em.createQuery(
                "select count(t) from Tramite t left join t.product p where " +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%'))"
        ).setParameter("query", query).getSingleResult()).intValue();
    }

    public int countTramitesBusquedaByEmpresa(String query, Long empresaId) {
        return ((Number) em.createQuery(
                "select count(t) from Tramite t left join t.product p where (" +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%')) ) and p.empresaId = :empresaId"
        ).setParameter("query", query).setParameter("empresaId", empresaId).getSingleResult()).intValue();
    }

    public int countTramitesBusquedaByUsuario(String query, Long usuarioId) {
        return ((Number) em.createQuery(
                "select count(t) from Tramite t left join t.product p where (" +
                        "lower(t.radicadoNumber) like lower(concat('%', :query, '%')) or " +
                        "lower(coalesce(p.nombre, '')) like lower(concat('%', :query, '%')) or " +
                        "lower(t.procedureType) like lower(concat('%', :query, '%')) ) and t.usuario.id = :usuarioId"
        ).setParameter("query", query).setParameter("usuarioId", usuarioId).getSingleResult()).intValue();
    }

    // ==================== BÚSQUEDA REGISTROS SANITARIOS ====================

    public List<Object[]> buscarRegistrosSanitarios(String query, int limit) {
        return em.createQuery(
                "select r.id, r.numeroRegistro, p.nombre, r.estado, r.fechaExpedicion, r.fechaVencimiento " +
                        "from RegistroSanitario r, Producto p where p.id = r.productoId and (" +
                        "lower(r.numeroRegistro) like lower(concat('%', :query, '%')) or " +
                        "lower(p.nombre) like lower(concat('%', :query, '%')) ) " +
                        "order by r.fechaExpedicion desc",
                Object[].class
        ).setParameter("query", query)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> buscarRegistrosSanitariosByEmpresa(String query, Long empresaId, int limit) {
        return em.createQuery(
                "select r.id, r.numeroRegistro, p.nombre, r.estado, r.fechaExpedicion, r.fechaVencimiento " +
                        "from RegistroSanitario r, Producto p where p.id = r.productoId and p.empresaId = :empresaId and (" +
                        "lower(r.numeroRegistro) like lower(concat('%', :query, '%')) or " +
                        "lower(p.nombre) like lower(concat('%', :query, '%')) ) " +
                        "order by r.fechaExpedicion desc",
                Object[].class
        ).setParameter("query", query)
                .setParameter("empresaId", empresaId)
                .setMaxResults(limit)
                .getResultList();
    }

    public int countRegistrosBusqueda(String query) {
        return ((Number) em.createQuery(
                "select count(r) from RegistroSanitario r, Producto p where p.id = r.productoId and (" +
                        "lower(r.numeroRegistro) like lower(concat('%', :query, '%')) or " +
                        "lower(p.nombre) like lower(concat('%', :query, '%')) )"
        ).setParameter("query", query).getSingleResult()).intValue();
    }

    public int countRegistrosBusquedaByEmpresa(String query, Long empresaId) {
        return ((Number) em.createQuery(
                "select count(r) from RegistroSanitario r, Producto p where p.id = r.productoId and p.empresaId = :empresaId and (" +
                        "lower(r.numeroRegistro) like lower(concat('%', :query, '%')) or " +
                        "lower(p.nombre) like lower(concat('%', :query, '%')) )"
        ).setParameter("query", query).setParameter("empresaId", empresaId).getSingleResult()).intValue();
    }

    // ==================== REGISTROS SANITARIOS RECIENTES ====================

    public List<Object[]> registrosSanitariosRecientes(int limit) {
        return em.createQuery(
                "select r.id, r.numeroRegistro, p.nombre, r.estado, r.fechaExpedicion, r.fechaVencimiento " +
                        "from RegistroSanitario r, Producto p where p.id = r.productoId " +
                        "order by r.fechaExpedicion desc",
                Object[].class
        ).setMaxResults(limit).getResultList();
    }

    public List<Object[]> registrosSanitariosRecientesByEmpresa(Long empresaId, int limit) {
        return em.createQuery(
                "select r.id, r.numeroRegistro, p.nombre, r.estado, r.fechaExpedicion, r.fechaVencimiento " +
                        "from RegistroSanitario r, Producto p where p.id = r.productoId and p.empresaId = :empresaId " +
                        "order by r.fechaExpedicion desc",
                Object[].class
        ).setParameter("empresaId", empresaId)
                .setMaxResults(limit)
                .getResultList();
    }

    // ==================== DETALLE TRÁMITE ====================

    public Object[] getTramiteCompleto(Long tramiteId) {
        return em.createQuery(
                "select t.id, t.radicadoNumber, t.submissionDate, t.procedureType, coalesce(p.nombre, 'Sin producto'), t.currentStatus, t.lastUpdate " +
                        "from Tramite t left join t.product p where t.id = :tramiteId",
                Object[].class
        ).setParameter("tramiteId", tramiteId).getSingleResult();
    }

    public List<Object[]> getEventosByTramite(Long tramiteId) {
        return em.createQuery(
                "select e.title, e.description, e.date, e.completed, e.currentEvent, e.orden " +
                        "from EventoTramite e where e.tramite.id = :tramiteId order by e.orden",
                Object[].class
        ).setParameter("tramiteId", tramiteId).getResultList();
    }

    public List<Object[]> getRequerimientosByTramite(Long tramiteId) {
        return em.createQuery(
                "select r.number, r.title, r.description, r.deadline, r.status " +
                        "from Requerimiento r where r.tramite.id = :tramiteId order by r.deadline",
                Object[].class
        ).setParameter("tramiteId", tramiteId).getResultList();
    }

    public List<Object[]> getNotificacionesByTramite(Long tramiteId, int limit) {
        return em.createQuery(
                "select n.type, n.title, n.message, n.date, n.read " +
                        "from Notificacion n where n.tramite.id = :tramiteId order by n.date desc",
                Object[].class
        ).setParameter("tramiteId", tramiteId).setMaxResults(limit).getResultList();
    }

    public List<Object[]> getHistorialByTramite(Long tramiteId) {
        return em.createQuery(
                "select h.fecha, h.accion, h.descripcion, h.usuario, h.estado " +
                        "from HistorialTramite h where h.tramiteId = :tramiteId order by h.fecha desc",
                Object[].class
        ).setParameter("tramiteId", tramiteId).getResultList();
    }
}
