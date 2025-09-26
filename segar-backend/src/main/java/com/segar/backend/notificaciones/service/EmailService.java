package com.segar.backend.notificaciones.service;

import com.segar.backend.notificaciones.api.dto.*;
import com.segar.backend.notificaciones.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de correos electrónicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailService {

    private final EmailRepository emailRepository;
    private final EmailSender emailSender;
    private final EmailReader emailReader;

    /**
     * Envía un correo electrónico
     */
    public EmailResponse sendEmail(SendEmailRequest request) throws EmailSendingException {
        log.info("Enviando correo con asunto: {}", request.getSubject());

        try {
            // Validar destinatarios
            List<EmailAddress> toAddresses = parseEmailAddresses(request.getToAddresses(), request.getToNames());
            List<EmailAddress> ccAddresses = parseEmailAddresses(request.getCcAddresses(), request.getCcNames());
            List<EmailAddress> bccAddresses = parseEmailAddresses(request.getBccAddresses(), request.getBccNames());

            // Crear contenido del correo
            EmailContent content = new EmailContent(
                request.getSubject(),
                request.getContent(),
                request.getIsHtml() != null ? request.getIsHtml() : false
            );

            // Crear registro del correo antes del envío
            Email email = createEmailRecord(request, toAddresses, ccAddresses, bccAddresses, content);
            email.setStatus(EmailStatus.QUEUED);
            email = emailRepository.save(email);

            // Enviar correo
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                emailSender.sendEmailWithAttachmentsAndInlineContent(
                    toAddresses, ccAddresses, bccAddresses, content,
                    request.getAttachments(), request.getInlineFiles()
                );
            } else {
                emailSender.sendEmail(toAddresses, ccAddresses, bccAddresses, content);
            }

            // Actualizar estado y fecha de envío
            email.setStatus(EmailStatus.SENT);
            email.setSentDate(LocalDateTime.now());

            // Guardar archivos adjuntos si existen
            if (request.getAttachments() != null) {
                saveAttachments(email, request.getAttachments(), false);
            }
            if (request.getInlineFiles() != null) {
                saveAttachments(email, request.getInlineFiles(), true);
            }

            email = emailRepository.save(email);
            log.info("Correo enviado exitosamente con ID: {}", email.getId());

            return mapToEmailResponse(email);

        } catch (Exception e) {
            log.error("Error al enviar correo: {}", e.getMessage(), e);
            throw new EmailSendingException("Error al enviar correo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene correos del buzón de entrada con filtros
     */
    @Transactional(readOnly = true)
    public Page<EmailResponse> getInboxEmails(EmailFilterRequest filter) {
        log.info("Obteniendo correos del buzón de entrada con filtros");

        Pageable pageable = createPageable(filter);

        Page<Email> emails = emailRepository.findByCriteria(
            filter.getFromAddress(),
            filter.getSubject(),
            filter.getType(),
            filter.getStatus(),
            filter.getIsRead(),
            filter.getStartDate(),
            filter.getEndDate(),
            pageable
        );

        return emails.map(this::mapToEmailResponse);
    }

    /**
     * Sincroniza correos desde el servidor
     */
    public void synchronizeEmails() {
        log.info("Sincronizando correos desde el servidor");

        try {
            List<Email> newEmails = emailReader.readNewEmails();

            for (Email email : newEmails) {
                // Verificar si el correo ya existe
                Optional<Email> existing = emailRepository.findByMessageId(email.getMessageId());
                if (existing.isEmpty()) {
                    email.setType(EmailType.INBOUND);
                    email.setStatus(EmailStatus.RECEIVED);
                    email.setReceivedDate(LocalDateTime.now());
                    emailRepository.save(email);
                    log.debug("Correo sincronizado: {}", email.getSubject());
                }
            }

            log.info("Sincronización completada. {} correos nuevos", newEmails.size());

        } catch (EmailReadingException e) {
            log.error("Error al sincronizar correos: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene un correo específico por ID
     */
    @Transactional(readOnly = true)
    public Optional<EmailResponse> getEmailById(Long id) {
        return emailRepository.findById(id).map(this::mapToEmailResponse);
    }

    /**
     * Marca un correo como leído
     */
    public void markEmailAsRead(Long id) {
        log.info("Marcando correo como leído: {}", id);

        Optional<Email> emailOpt = emailRepository.findById(id);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();
            email.markAsRead();
            emailRepository.save(email);

            // Intentar marcar en el servidor si es un correo recibido
            if (email.getType() == EmailType.INBOUND && email.getMessageId() != null) {
                try {
                    emailReader.markEmailAsRead(email.getMessageId());
                } catch (EmailReadingException e) {
                    log.warn("No se pudo marcar el correo como leído en el servidor: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Marca un correo como no leído
     */
    public void markEmailAsUnread(Long id) {
        log.info("Marcando correo como no leído: {}", id);

        Optional<Email> emailOpt = emailRepository.findById(id);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();
            email.markAsUnread();
            emailRepository.save(email);

            // Intentar marcar en el servidor si es un correo recibido
            if (email.getType() == EmailType.INBOUND && email.getMessageId() != null) {
                try {
                    emailReader.markEmailAsUnread(email.getMessageId());
                } catch (EmailReadingException e) {
                    log.warn("No se pudo marcar el correo como no leído en el servidor: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Elimina un correo
     */
    public void deleteEmail(Long id) {
        log.info("Eliminando correo: {}", id);

        Optional<Email> emailOpt = emailRepository.findById(id);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();

            // Intentar eliminar del servidor si es un correo recibido
            if (email.getType() == EmailType.INBOUND && email.getMessageId() != null) {
                try {
                    emailReader.deleteEmailFromServer(email.getMessageId());
                } catch (EmailReadingException e) {
                    log.warn("No se pudo eliminar el correo del servidor: {}", e.getMessage());
                }
            }

            emailRepository.delete(email);
        }
    }

    /**
     * Obtiene correos enviados
     */
    @Transactional(readOnly = true)
    public Page<EmailResponse> getSentEmails(Pageable pageable) {
        Page<Email> emails = emailRepository.findByTypeOrderByCreatedAtDesc(EmailType.OUTBOUND, pageable);
        return emails.map(this::mapToEmailResponse);
    }

    /**
     * Obtiene el conteo de correos no leídos
     */
    @Transactional(readOnly = true)
    public long getUnreadEmailCount() {
        return emailRepository.countByIsReadFalse();
    }

    /**
     * Obtiene el archivo adjunto de un correo
     */
    @Transactional(readOnly = true)
    public Optional<EmailAttachment> getEmailAttachment(Long emailId, Long attachmentId) {
        Optional<Email> emailOpt = emailRepository.findById(emailId);
        if (emailOpt.isPresent()) {
            return emailOpt.get().getAttachments().stream()
                .filter(att -> att.getId().equals(attachmentId))
                .findFirst();
        }
        return Optional.empty();
    }

    // Métodos privados de utilidad

    private List<EmailAddress> parseEmailAddresses(List<String> addresses, List<String> names) {
        if (addresses == null || addresses.isEmpty()) {
            return new ArrayList<>();
        }

        List<EmailAddress> result = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            String address = addresses.get(i);
            String name = (names != null && i < names.size()) ? names.get(i) : null;
            result.add(new EmailAddress(address, name));
        }
        return result;
    }

    private Email createEmailRecord(SendEmailRequest request, List<EmailAddress> toAddresses,
                                   List<EmailAddress> ccAddresses, List<EmailAddress> bccAddresses,
                                   EmailContent content) {
        return Email.builder()
            .fromAddress("soportecasalunaairbnb@gmail.com") // From configurado
            .toAddresses(toAddresses.stream().map(EmailAddress::getAddress).collect(Collectors.joining(", ")))
            .ccAddresses(ccAddresses.isEmpty() ? null : ccAddresses.stream().map(EmailAddress::getAddress).collect(Collectors.joining(", ")))
            .bccAddresses(bccAddresses.isEmpty() ? null : bccAddresses.stream().map(EmailAddress::getAddress).collect(Collectors.joining(", ")))
            .subject(content.getSubject())
            .content(content.getBody())
            .isHtml(content.isHtml())
            .isRead(true) // Los correos enviados se marcan como leídos
            .type(EmailType.OUTBOUND)
            .status(EmailStatus.DRAFT)
            .build();
    }

    private void saveAttachments(Email email, List<MultipartFile> files, boolean isInline) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    EmailAttachment attachment = EmailAttachment.builder()
                        .email(email)
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .fileContent(file.getBytes())
                        .isInline(isInline)
                        .build();

                    email.addAttachment(attachment);
                } catch (Exception e) {
                    log.error("Error al guardar archivo adjunto: {}", e.getMessage(), e);
                }
            }
        }
    }

    private Pageable createPageable(EmailFilterRequest filter) {
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(filter.getSortDirection()) ?
                Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSortBy()
        );
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    private EmailResponse mapToEmailResponse(Email email) {
        List<EmailAttachmentDto> attachmentDtos = email.getAttachments().stream()
            .map(this::mapToAttachmentDto)
            .collect(Collectors.toList());

        return EmailResponse.builder()
            .id(email.getId())
            .fromAddress(email.getFromAddress())
            .toAddresses(parseAddressList(email.getToAddresses()))
            .ccAddresses(parseAddressList(email.getCcAddresses()))
            .bccAddresses(parseAddressList(email.getBccAddresses()))
            .subject(email.getSubject())
            .content(email.getContent())
            .isHtml(email.getIsHtml())
            .isRead(email.getIsRead())
            .status(email.getStatus())
            .type(email.getType())
            .sentDate(email.getSentDate())
            .receivedDate(email.getReceivedDate())
            .createdAt(email.getCreatedAt())
            .updatedAt(email.getUpdatedAt())
            .attachments(attachmentDtos)
            .messageId(email.getMessageId())
            .inReplyTo(email.getInReplyTo())
            .errorMessage(email.getErrorMessage())
            .attachmentCount(attachmentDtos.size())
            .build();
    }

    private EmailAttachmentDto mapToAttachmentDto(EmailAttachment attachment) {
        return EmailAttachmentDto.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .contentType(attachment.getContentType())
            .fileSize(attachment.getFileSize())
            .contentId(attachment.getContentId())
            .isInline(attachment.getIsInline())
            .createdAt(attachment.getCreatedAt())
            .downloadUrl("/api/notifications/emails/" + attachment.getEmail().getId() + "/attachments/" + attachment.getId() + "/download")
            .build();
    }

    private List<String> parseAddressList(String addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(addresses.split(",\\s*"));
    }
}
