package com.segar.backend.notificaciones.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio para la gestión de correos electrónicos
 */
@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    // Buscar correos por tipo (enviados o recibidos)
    Page<Email> findByTypeOrderByCreatedAtDesc(EmailType type, Pageable pageable);

    // Buscar correos por estado
    Page<Email> findByStatusOrderByCreatedAtDesc(EmailStatus status, Pageable pageable);

    // Buscar correos no leídos
    Page<Email> findByIsReadFalseOrderByCreatedAtDesc(Pageable pageable);

    // Buscar correos por remitente
    Page<Email> findByFromAddressContainingIgnoreCaseOrderByCreatedAtDesc(String fromAddress, Pageable pageable);

    // Buscar correos por asunto
    Page<Email> findBySubjectContainingIgnoreCaseOrderByCreatedAtDesc(String subject, Pageable pageable);

    // Buscar correos en un rango de fechas
    @Query("SELECT e FROM Email e WHERE e.createdAt BETWEEN :startDate AND :endDate ORDER BY e.createdAt DESC")
    Page<Email> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               Pageable pageable);

    // Buscar correos por múltiples criterios
    @Query("SELECT e FROM Email e WHERE " +
           "(:fromAddress IS NULL OR LOWER(e.fromAddress) LIKE LOWER(CONCAT('%', :fromAddress, '%'))) AND " +
           "(:subject IS NULL OR LOWER(e.subject) LIKE LOWER(CONCAT('%', :subject, '%'))) AND " +
           "(:type IS NULL OR e.type = :type) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:isRead IS NULL OR e.isRead = :isRead) AND " +
           "(:startDate IS NULL OR e.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR e.createdAt <= :endDate) " +
           "ORDER BY e.createdAt DESC")
    Page<Email> findByCriteria(@Param("fromAddress") String fromAddress,
                              @Param("subject") String subject,
                              @Param("type") EmailType type,
                              @Param("status") EmailStatus status,
                              @Param("isRead") Boolean isRead,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              Pageable pageable);

    // Contar correos no leídos
    long countByIsReadFalse();

    // Buscar correo por messageId (para evitar duplicados)
    Optional<Email> findByMessageId(String messageId);

    // Buscar correos con archivos adjuntos
    @Query("SELECT DISTINCT e FROM Email e JOIN e.attachments a ORDER BY e.createdAt DESC")
    Page<Email> findEmailsWithAttachments(Pageable pageable);

    // Eliminar correos antiguos
    @Modifying
    @Query("DELETE FROM Email e WHERE e.createdAt < :cutoffDate AND e.status = :status")
    void deleteOldEmails(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("status") EmailStatus status);

    // Obtener la fecha del correo más reciente en BD
    @Query("SELECT MAX(e.receivedDate) FROM Email e WHERE e.type = 'INBOUND'")
    Optional<LocalDateTime> findLatestReceivedDate();

    // Verificar si existe un correo por messageId (más eficiente que findByMessageId)
    boolean existsByMessageId(String messageId);

    // Obtener todos los messageIds existentes (para batch checking)
    @Query("SELECT e.messageId FROM Email e WHERE e.messageId IS NOT NULL AND e.type = 'INBOUND'")
    List<String> findAllMessageIds();

    // Buscar correos enviados ESTRICTAMENTE por la aplicación (OUTBOUND + remitente específico + estado SENT)
    @Query("SELECT e FROM Email e WHERE e.type = :type AND e.status = :status AND e.fromAddress = :fromAddress ORDER BY e.sentDate DESC, e.createdAt DESC")
    Page<Email> findSentEmailsByFromAddressAndStatus(@Param("type") EmailType type,
                                                      @Param("status") EmailStatus status,
                                                      @Param("fromAddress") String fromAddress,
                                                      Pageable pageable);

    // Buscar correos por tipo y estado (fallback para correos enviados sin filtro de fromAddress)
    @Query("SELECT e FROM Email e WHERE e.type = :type AND e.status = :status ORDER BY e.sentDate DESC, e.createdAt DESC")
    Page<Email> findByTypeAndStatusOrderBySentDateDescCreatedAtDesc(@Param("type") EmailType type,
                                                                     @Param("status") EmailStatus status,
                                                                     Pageable pageable);
}
