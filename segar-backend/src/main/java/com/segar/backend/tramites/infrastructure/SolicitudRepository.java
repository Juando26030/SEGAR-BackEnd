package com.segar.backend.tramites.infrastructure;

import com.segar.backend.shared.domain.EstadoSolicitud;
import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.tramites.domain.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Solicitud
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /**
     * Busca solicitudes por ID de empresa
     * @param empresaId ID de la empresa
     * @return Lista de solicitudes de la empresa
     */
    List<Solicitud> findByEmpresaId(Long empresaId);

    /**
     * Busca solicitudes por estado
     * @param estado Estado de la solicitud
     * @return Lista de solicitudes con el estado especificado
     */
    List<Solicitud> findByEstado(EstadoSolicitud estado);

    /**
     * Verifica si existe una solicitud con el número de radicado especificado
     * @param numeroRadicado Número de radicado a verificar
     * @return true si existe, false si no
     */
    boolean existsByNumeroRadicado(String numeroRadicado);

    /**
     * Busca solicitud por número de radicado
     * @param numeroRadicado Número de radicado
     * @return Optional con la solicitud si existe
     */
    Optional<Solicitud> findByNumeroRadicado(String numeroRadicado);

    /**
     * Verifica si existe solicitud radicada para el mismo producto y tipo de trámite
     * @param productoId ID del producto
     * @param tipoTramite Tipo de trámite
     * @param estado Estado de la solicitud
     * @return true si existe solicitud duplicada
     */
    @Query("SELECT COUNT(s) > 0 FROM Solicitud s WHERE s.producto.id = :productoId " +
           "AND s.tipoTramite = :tipoTramite AND s.estado = :estado")
    boolean existsByProductoIdAndTipoTramiteAndEstado(
        @Param("productoId") Long productoId,
        @Param("tipoTramite") TipoTramite tipoTramite,
        @Param("estado") EstadoSolicitud estado
    );

    /**
     * Busca solicitudes por empresa y estado
     * @param empresaId ID de la empresa
     * @param estado Estado de la solicitud
     * @return Lista de solicitudes filtradas
     */
    List<Solicitud> findByEmpresaIdAndEstado(Long empresaId, EstadoSolicitud estado);

    /**
     * Busca solicitud por empresa, producto y tipo de trámite
     * Usado para validar duplicados en radicación
     * @param empresaId ID de la empresa
     * @param productoId ID del producto
     * @param tipoTramite Tipo de trámite
     * @return Optional con la solicitud si existe
     */
    @Query("SELECT s FROM Solicitud s WHERE s.empresaId = :empresaId " +
           "AND s.producto.id = :productoId AND s.tipoTramite = :tipoTramite")
    Optional<Solicitud> findByEmpresaIdAndProductoIdAndTipoTramite(
        @Param("empresaId") Long empresaId,
        @Param("productoId") Long productoId,
        @Param("tipoTramite") TipoTramite tipoTramite
    );
}
