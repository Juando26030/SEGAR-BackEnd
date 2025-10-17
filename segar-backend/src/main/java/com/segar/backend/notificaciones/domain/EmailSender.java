package com.segar.backend.notificaciones.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Puerto para el envío de correos electrónicos
 */
public interface EmailSender {

    /**
     * Envía un correo electrónico simple
     */
    void sendSimpleEmail(EmailAddress to, EmailContent content) throws EmailSendingException;

    /**
     * Envía un correo electrónico a múltiples destinatarios
     */
    void sendEmail(List<EmailAddress> to,
                   List<EmailAddress> cc,
                   List<EmailAddress> bcc,
                   EmailContent content) throws EmailSendingException;

    /**
     * Envía un correo electrónico con archivos adjuntos
     */
    void sendEmailWithAttachments(List<EmailAddress> to,
                                  List<EmailAddress> cc,
                                  List<EmailAddress> bcc,
                                  EmailContent content,
                                  List<MultipartFile> attachments) throws EmailSendingException;

    /**
     * Envía un correo electrónico con archivos adjuntos y contenido embebido
     */
    void sendEmailWithAttachmentsAndInlineContent(List<EmailAddress> to,
                                                  List<EmailAddress> cc,
                                                  List<EmailAddress> bcc,
                                                  EmailContent content,
                                                  List<MultipartFile> attachments,
                                                  List<MultipartFile> inlineFiles) throws EmailSendingException;

    /**
     * Verifica si el servicio de correo está disponible
     */
    boolean isEmailServiceAvailable();
}
