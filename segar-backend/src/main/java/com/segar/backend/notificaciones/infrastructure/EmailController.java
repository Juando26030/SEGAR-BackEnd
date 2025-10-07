package com.segar.backend.notificaciones.infrastructure;

import com.segar.backend.notificaciones.api.dto.*;
import com.segar.backend.notificaciones.domain.EmailAttachment;
import com.segar.backend.notificaciones.domain.EmailReader;
import com.segar.backend.notificaciones.domain.EmailSendingException;
import com.segar.backend.notificaciones.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de correos electrónicos
 */
@RestController
@RequestMapping("/api/notifications/emails")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Email Management", description = "APIs para la gestión completa de correos electrónicos")
public class EmailController {

    private final EmailService emailService;
    private final EmailReader emailReader;

    @PostMapping("/send")
    @Operation(summary = "Enviar correo electrónico (único endpoint flexible)",
               description = "Envía correos con todos los parámetros opcionales: destinatarios múltiples, CC, BCC, HTML, adjuntos")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        try {
            log.info("Enviando correo a: {}, asunto: {}", request.getToAddresses(), request.getSubject());
            EmailResponse response = emailService.sendEmail(request);
            return ResponseEntity.ok(response);
        } catch (EmailSendingException e) {
            log.error("Error enviando correo: {}", e.getMessage(), e);

            // Detectar error de autenticación de Gmail
            if (e.getMessage().contains("Authentication") || e.getMessage().contains("535")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("GMAIL_AUTH_ERROR",
                        "Error de autenticación con Gmail. Necesitas usar una 'Contraseña de Aplicación' de Gmail, no tu contraseña normal. " +
                        "Ve a: https://myaccount.google.com/apppasswords y genera una nueva contraseña de aplicación."));
            }

            return ResponseEntity.badRequest()
                .body(new ErrorResponse("EMAIL_SEND_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado enviando correo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/inbox")
    @Operation(summary = "Buscar correos con filtros avanzados (recomendado)",
               description = "Busca TODOS los correos o con filtros específicos: texto general, remitente, asunto, fechas, estado de lectura, adjuntos. " +
                           "Consulta la BD local (instantáneo). Usa /sync-async para actualizar desde el servidor.")
    public ResponseEntity<Page<EmailResponse>> searchEmails(
            @Parameter(description = "Filtros de búsqueda (todos opcionales)") @RequestBody(required = false) EmailSearchFilter searchFilter) {
        try {
            // Si no se envían filtros, crear uno vacío para traer todos los correos
            if (searchFilter == null) {
                searchFilter = EmailSearchFilter.builder()
                    .page(0)
                    .size(20)
                    .sortBy("receivedDate")
                    .sortDirection("DESC")
                    .build();
            }

            log.info("Búsqueda de correos - Filtros: searchText='{}', fromAddress='{}', isRead={}",
                searchFilter.getSearchText(), searchFilter.getFromAddress(), searchFilter.getIsRead());

            Page<EmailResponse> emails = emailService.searchEmails(searchFilter);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error en búsqueda de correos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inbox")
    @Operation(summary = "Obtener correos con parámetros GET (alternativa simple)",
               description = "Obtiene correos usando parámetros de URL. Para filtros avanzados usa POST /inbox")
    public ResponseEntity<Page<EmailResponse>> getInboxEmails(
            @RequestParam(required = false) String fromAddress,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "receivedDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            // Construir filtro desde parámetros GET
            EmailSearchFilter searchFilter = EmailSearchFilter.builder()
                .searchText(searchText)
                .fromAddress(fromAddress)
                .subject(subject)
                .isRead(isRead)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

            Page<EmailResponse> emails = emailService.searchEmails(searchFilter);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo correos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener correo específico")
    public ResponseEntity<?> getEmailById(@PathVariable Long id) {
        try {
            Optional<EmailResponse> email = emailService.getEmailById(id);
            return email.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error obteniendo correo por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @PutMapping("/{id}/mark-read")
    @Operation(summary = "Marcar correo como leído")
    public ResponseEntity<?> markEmailAsRead(@PathVariable Long id) {
        try {
            emailService.markEmailAsRead(id);
            return ResponseEntity.ok(new SuccessResponse("Correo marcado como leído exitosamente"));
        } catch (Exception e) {
            log.error("Error marcando correo como leído {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @PutMapping("/{id}/mark-unread")
    @Operation(summary = "Marcar correo como no leído")
    public ResponseEntity<?> markEmailAsUnread(@PathVariable Long id) {
        try {
            emailService.markEmailAsUnread(id);
            return ResponseEntity.ok(new SuccessResponse("Correo marcado como no leído exitosamente"));
        } catch (Exception e) {
            log.error("Error marcando correo como no leído {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar correo")
    public ResponseEntity<?> deleteEmail(@PathVariable Long id) {
        try {
            emailService.deleteEmail(id);
            return ResponseEntity.ok(new SuccessResponse("Correo eliminado exitosamente"));
        } catch (Exception e) {
            log.error("Error eliminando correo {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @GetMapping("/sent")
    @Operation(summary = "Obtener correos enviados")
    public ResponseEntity<Page<EmailResponse>> getSentEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EmailResponse> emails = emailService.getSentEmails(pageable);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo correos enviados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Obtener cantidad de correos no leídos")
    public ResponseEntity<Long> getUnreadEmailCount() {
        try {
            long count = emailService.getUnreadEmailCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error obteniendo conteo de correos no leídos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar correos manualmente",
               description = "Trae los correos nuevos del servidor. Usa este endpoint cuando quieras actualizar la lista de correos.")
    public ResponseEntity<?> synchronizeEmails() {
        try {
            log.info("🔄 Sincronización manual solicitada");
            long startTime = System.currentTimeMillis();

            emailService.synchronizeEmails();

            long duration = System.currentTimeMillis() - startTime;
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sincronización completada exitosamente",
                "duration", duration + "ms"
            ));
        } catch (Exception e) {
            log.error("Error sincronizando correos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SYNC_ERROR", "Error sincronizando correos: " + e.getMessage()));
        }
    }

    @PostMapping("/sync-async")
    @Operation(summary = "Sincronizar correos en segundo plano (rápido)",
               description = "Inicia la sincronización en segundo plano y retorna inmediatamente. Ideal para no bloquear la UI.")
    public ResponseEntity<?> synchronizeEmailsAsync() {
        try {
            log.info("🚀 Sincronización asíncrona solicitada");
            emailService.synchronizeEmailsAsync();

            return ResponseEntity.accepted().body(Map.of(
                "status", "accepted",
                "message", "Sincronización iniciada en segundo plano. Los correos aparecerán en breve."
            ));
        } catch (Exception e) {
            log.error("Error iniciando sincronización asíncrona: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SYNC_ERROR", "Error iniciando sincronización: " + e.getMessage()));
        }
    }

    @GetMapping("/sync-status")
    @Operation(summary = "Ver estadísticas de correos sincronizados")
    public ResponseEntity<?> getSyncStatus() {
        try {
            long totalEmails = emailService.getTotalEmailCount();
            long unreadEmails = emailService.getUnreadEmailCount();

            return ResponseEntity.ok(Map.of(
                "totalEmails", totalEmails,
                "unreadEmails", unreadEmails,
                "readEmails", totalEmails - unreadEmails,
                "lastSync", "Ver logs del servidor"
            ));
        } catch (Exception e) {
            log.error("Error obteniendo estado de sincronización: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{emailId}/attachments/{attachmentId}/download")
    @Operation(summary = "Descargar archivo adjunto")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long emailId, @PathVariable Long attachmentId) {
        try {
            Optional<EmailAttachment> attachmentOpt = emailService.getEmailAttachment(emailId, attachmentId);

            if (attachmentOpt.isPresent()) {
                EmailAttachment attachment = attachmentOpt.get();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(attachment.getContentType()));
                headers.setContentDispositionFormData("attachment", attachment.getFileName());
                headers.setContentLength(attachment.getFileSize());

                return ResponseEntity.ok().headers(headers).body(attachment.getFileContent());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error descargando archivo adjunto {}/{}: {}", emailId, attachmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{emailId}/attachments/{attachmentId}/preview")
    @Operation(summary = "Previsualizar archivo adjunto")
    public ResponseEntity<byte[]> previewAttachment(@PathVariable Long emailId, @PathVariable Long attachmentId) {
        try {
            Optional<EmailAttachment> attachmentOpt = emailService.getEmailAttachment(emailId, attachmentId);

            if (attachmentOpt.isPresent()) {
                EmailAttachment attachment = attachmentOpt.get();

                if (!attachment.isImage()) {
                    return ResponseEntity.badRequest().build();
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(attachment.getContentType()));
                headers.setContentLength(attachment.getFileSize());

                return ResponseEntity.ok().headers(headers).body(attachment.getFileContent());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error previsualizando archivo adjunto {}/{}: {}", emailId, attachmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test-imap-connection")
    @Operation(summary = "Probar conexión IMAP")
    public ResponseEntity<?> testImapConnection() {
        try {
            boolean connected = emailReader.isConnected();
            emailReader.synchronizeEmails();

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Conexión IMAP exitosa",
                "connected", connected
            ));
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("AUTHENTICATIONFAILED")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "errorType", "AUTHENTICATION_FAILED",
                    "message", "Error de autenticación Gmail",
                    "solution", "Necesitas configurar una contraseña de aplicación para Gmail"
                ));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Error de conexión: " + errorMessage
            ));
        }
    }

    // DTOs para respuestas
    public static class ErrorResponse {
        public String errorCode;
        public String message;

        public ErrorResponse(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
    }

    public static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
