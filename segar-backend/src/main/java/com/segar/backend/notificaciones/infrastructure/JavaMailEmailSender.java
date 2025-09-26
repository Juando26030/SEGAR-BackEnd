package com.segar.backend.notificaciones.infrastructure;

import com.segar.backend.notificaciones.domain.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del puerto EmailSender usando JavaMail
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JavaMailEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendSimpleEmail(EmailAddress to, EmailContent content) throws EmailSendingException {
        sendEmail(List.of(to), null, null, content);
    }

    @Override
    public void sendEmail(List<EmailAddress> to, List<EmailAddress> cc, List<EmailAddress> bcc,
                         EmailContent content) throws EmailSendingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configurar destinatarios
            helper.setTo(to.stream().map(EmailAddress::getAddress).toArray(String[]::new));

            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.stream().map(EmailAddress::getAddress).toArray(String[]::new));
            }

            if (bcc != null && !bcc.isEmpty()) {
                helper.setBcc(bcc.stream().map(EmailAddress::getAddress).toArray(String[]::new));
            }

            // Configurar contenido
            helper.setSubject(content.getSubject());
            helper.setText(content.getBody(), content.isHtml());

            // Enviar
            javaMailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", to);

        } catch (MessagingException | MailException e) {
            log.error("Error al enviar correo: {}", e.getMessage(), e);
            throw new EmailSendingException("Error al enviar correo: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendEmailWithAttachments(List<EmailAddress> to, List<EmailAddress> cc,
                                       List<EmailAddress> bcc, EmailContent content,
                                       List<MultipartFile> attachments) throws EmailSendingException {
        sendEmailWithAttachmentsAndInlineContent(to, cc, bcc, content, attachments, null);
    }

    @Override
    public void sendEmailWithAttachmentsAndInlineContent(List<EmailAddress> to, List<EmailAddress> cc,
                                                        List<EmailAddress> bcc, EmailContent content,
                                                        List<MultipartFile> attachments,
                                                        List<MultipartFile> inlineFiles) throws EmailSendingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configurar destinatarios
            helper.setTo(to.stream().map(EmailAddress::getAddress).toArray(String[]::new));

            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.stream().map(EmailAddress::getAddress).toArray(String[]::new));
            }

            if (bcc != null && !bcc.isEmpty()) {
                helper.setBcc(bcc.stream().map(EmailAddress::getAddress).toArray(String[]::new));
            }

            // Configurar contenido
            helper.setSubject(content.getSubject());

            String emailContent = content.getBody();

            // Procesar archivos inline si existen
            if (inlineFiles != null && !inlineFiles.isEmpty()) {
                for (MultipartFile inlineFile : inlineFiles) {
                    if (!inlineFile.isEmpty()) {
                        String contentId = "inline_" + UUID.randomUUID().toString();
                        helper.addInline(contentId, new ByteArrayResource(inlineFile.getBytes()),
                                       inlineFile.getContentType());

                        // Si es HTML, reemplazar referencias a la imagen
                        if (content.isHtml()) {
                            emailContent = emailContent.replace(
                                "src=\"" + inlineFile.getOriginalFilename() + "\"",
                                "src=\"cid:" + contentId + "\""
                            );
                        }
                    }
                }
            }

            helper.setText(emailContent, content.isHtml());

            // Agregar archivos adjuntos
            if (attachments != null && !attachments.isEmpty()) {
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        helper.addAttachment(
                            attachment.getOriginalFilename(),
                            new ByteArrayResource(attachment.getBytes()),
                            attachment.getContentType()
                        );
                    }
                }
            }

            // Enviar
            javaMailSender.send(message);
            log.info("Correo con archivos adjuntos enviado exitosamente a: {}", to);

        } catch (Exception e) {
            log.error("Error al enviar correo con archivos adjuntos: {}", e.getMessage(), e);
            throw new EmailSendingException("Error al enviar correo con archivos adjuntos: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isEmailServiceAvailable() {
        try {
            // Verificar conectividad creando un mensaje de prueba
            MimeMessage testMessage = javaMailSender.createMimeMessage();
            return testMessage != null;
        } catch (Exception e) {
            log.warn("Servicio de correo no disponible: {}", e.getMessage());
            return false;
        }
    }
}
