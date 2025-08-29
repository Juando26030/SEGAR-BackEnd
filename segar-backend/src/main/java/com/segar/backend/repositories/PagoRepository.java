package com.segar.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.segar.backend.models.Pago;
import com.segar.backend.models.EstadoPago;
import com.segar.backend.models.MetodoPago;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago
 * Implementado para el Paso 5: Radicación de la Solicitud
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Busca pago por referencia de pago
     * @param referenciaPago Referencia del pago
     * @return Optional con el pago si existe
     */
    Optional<Pago> findByReferenciaPago(String referenciaPago);

    /**
     * Busca pagos por estado
     * @param estado Estado del pago
     * @return Lista de pagos con el estado especificado
     */
    List<Pago> findByEstado(EstadoPago estado);

    /**
     * Busca pagos por método de pago
     * @param metodoPago Método de pago
     * @return Lista de pagos con el método especificado
     */
    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    /**
     * Verifica si existe un pago aprobado para una solicitud
     * @param solicitudId ID de la solicitud
     * @return true si existe pago aprobado
     */
    @Query("SELECT COUNT(p) > 0 FROM Pago p WHERE p.solicitud.id = :solicitudId AND p.estado = 'APROBADO'")
    boolean existePagoAprobadoPorSolicitud(@Param("solicitudId") Long solicitudId);

    /**
     * Busca pago por ID de solicitud
     * @param solicitudId ID de la solicitud
     * @return Optional con el pago si existe
     */
    @Query("SELECT p FROM Pago p WHERE p.solicitud.id = :solicitudId")
    Optional<Pago> findBySolicitudId(@Param("solicitudId") Long solicitudId);
}
