package com.segar.backend.notificaciones.infrastructure;

import com.segar.backend.notificaciones.api.dto.*;
import com.segar.backend.notificaciones.domain.EmailAttachment;
import com.segar.backend.notificaciones.domain.EmailReader;
import com.segar.backend.notificaciones.domain.EmailSendingException;
import com.segar.backend.notificaciones.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.multipart.MultipartFile;

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
    @Operation(summary = "Enviar correo electrónico",
               description = "Envía un correo electrónico con soporte para múltiples destinatarios, archivos adjuntos e imágenes embebidas")
    @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<?> sendEmail(
            @Parameter(description = "Datos del correo a enviar") @Valid @ModelAttribute SendEmailRequest request) {
        try {
            EmailResponse response = emailService.sendEmail(request);
            return ResponseEntity.ok(response);
        } catch (EmailSendingException e) {
            log.error("Error enviando correo: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("EMAIL_SEND_ERROR", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado enviando correo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @GetMapping("/inbox")
    @Operation(summary = "Obtener correos del buzón de entrada",
               description = "Obtiene correos del buzón de entrada con filtros y paginación")
    public ResponseEntity<Page<EmailResponse>> getInboxEmails(
            @Parameter(description = "Filtros de búsqueda") @ModelAttribute EmailFilterRequest filter) {
        try {
            Page<EmailResponse> emails = emailService.getInboxEmails(filter);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo correos del buzón: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/inbox")
    @Operation(summary = "Obtener correos del buzón con filtros específicos",
               description = "Obtiene correos del buzón de entrada con filtros avanzados enviados en el cuerpo de la petición")
    public ResponseEntity<Page<EmailResponse>> getInboxEmailsWithFilters(
            @Parameter(description = "Filtros de búsqueda específicos") @RequestBody EmailFilterRequest filter) {
        try {
            log.info("Obteniendo correos del buzón con filtros: fromAddress={}, subject={}, type={}",
                filter.getFromAddress(), filter.getSubject(), filter.getType());

            Page<EmailResponse> emails = emailService.getInboxEmails(filter);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo correos del buzón con filtros: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inbox/received")
    @Operation(summary = "Obtener solo correos recibidos (INBOUND)",
               description = "Obtiene únicamente correos recibidos desde el servidor de correo")
    public ResponseEntity<?> getReceivedEmails(
            @Parameter(description = "Filtros de búsqueda") @ModelAttribute EmailFilterRequest filter) {
        try {
            Page<EmailResponse> emails = emailService.getInboundEmails(filter);

            if (emails.isEmpty()) {
                return ResponseEntity.ok().body(Map.of(
                    "content", emails.getContent(),
                    "message", "No hay correos recibidos. Ejecute POST /api/notifications/emails/sync para sincronizar con Gmail",
                    "suggestion", "Primero sincronice los correos ejecutando: POST /api/notifications/emails/sync",
                    "pageable", emails.getPageable(),
                    "totalElements", emails.getTotalElements()
                ));
            }

            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo correos recibidos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Obtener todos los correos",
               description = "Obtiene todos los correos (enviados y recibidos)")
    public ResponseEntity<Page<EmailResponse>> getAllEmails(
            @Parameter(description = "Filtros de búsqueda") @ModelAttribute EmailFilterRequest filter) {
        try {
            Page<EmailResponse> emails = emailService.getAllEmails(filter);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            log.error("Error obteniendo todos los correos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener correo específico", description = "Obtiene un correo específico por su ID")
    public ResponseEntity<?> getEmailById(@PathVariable Long id) {
        try {
            Optional<EmailResponse> email = emailService.getEmailById(id);
            if (email.isPresent()) {
                return ResponseEntity.ok(email.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error obteniendo correo por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @PutMapping("/{id}/mark-read")
    @Operation(summary = "Marcar correo como leído", description = "Marca un correo específico como leído")
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
    @Operation(summary = "Marcar correo como no leído", description = "Marca un correo específico como no leído")
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
    @Operation(summary = "Eliminar correo", description = "Elimina un correo específico")
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
    @Operation(summary = "Obtener correos enviados", description = "Obtiene la lista de correos enviados con paginación")
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
    @Operation(summary = "Obtener cantidad de correos no leídos", description = "Obtiene el número de correos no leídos")
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
    @Operation(summary = "Sincronizar correos", description = "Sincroniza correos desde el servidor de correo")
    public ResponseEntity<?> synchronizeEmails() {
        try {
            emailService.synchronizeEmails();
            return ResponseEntity.ok(new SuccessResponse("Sincronización completada exitosamente"));
        } catch (Exception e) {
            log.error("Error sincronizando correos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SYNC_ERROR", "Error sincronizando correos: " + e.getMessage()));
        }
    }

    @PostMapping("/sync-auto")
    @Operation(summary = "Sincronización automática", description = "Realiza sincronización automática cada cierto tiempo")
    public ResponseEntity<?> enableAutoSync() {
        try {
            // Esto podría implementarse con @Scheduled si se requiere
            emailService.synchronizeEmails();
            return ResponseEntity.ok(new SuccessResponse("Auto-sincronización iniciada"));
        } catch (Exception e) {
            log.error("Error en auto-sincronización: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("AUTO_SYNC_ERROR", "Error en auto-sincronización: " + e.getMessage()));
        }
    }

    @GetMapping("/{emailId}/attachments/{attachmentId}/download")
    @Operation(summary = "Descargar archivo adjunto", description = "Descarga un archivo adjunto específico")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long emailId,
            @PathVariable Long attachmentId) {
        try {
            Optional<EmailAttachment> attachmentOpt = emailService.getEmailAttachment(emailId, attachmentId);

            if (attachmentOpt.isPresent()) {
                EmailAttachment attachment = attachmentOpt.get();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(attachment.getContentType()));
                headers.setContentDispositionFormData("attachment", attachment.getFileName());
                headers.setContentLength(attachment.getFileSize());

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(attachment.getFileContent());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error descargando archivo adjunto {}/{}: {}", emailId, attachmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{emailId}/attachments/{attachmentId}/preview")
    @Operation(summary = "Previsualizar archivo adjunto", description = "Previsualiza un archivo adjunto (para imágenes)")
    public ResponseEntity<byte[]> previewAttachment(
            @PathVariable Long emailId,
            @PathVariable Long attachmentId) {
        try {
            Optional<EmailAttachment> attachmentOpt = emailService.getEmailAttachment(emailId, attachmentId);

            if (attachmentOpt.isPresent()) {
                EmailAttachment attachment = attachmentOpt.get();

                // Solo permitir previsualización de imágenes
                if (!attachment.isImage()) {
                    return ResponseEntity.badRequest().build();
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(attachment.getContentType()));
                headers.setContentLength(attachment.getFileSize());

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(attachment.getFileContent());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error previsualizando archivo adjunto {}/{}: {}", emailId, attachmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test-imap-connection")
    @Operation(summary = "Probar conexión IMAP", description = "Prueba la conexión IMAP con Gmail para diagnóstico")
    public ResponseEntity<?> testImapConnection() {
        try {
            boolean connected = emailReader.isConnected();

            // Intentar conectar manualmente
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
                    "solution", "Necesitas configurar una contraseña de aplicación para Gmail",
                    "steps", List.of(
                        "1. Ve a tu cuenta de Google",
                        "2. Activa autenticación de 2 factores",
                        "3. Genera una contraseña de aplicación específica",
                        "4. Usa esa contraseña en spring.mail.imap.password"
                    )
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

        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
    }

    public static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        // Getter
        public String getMessage() { return message; }
    }
}
