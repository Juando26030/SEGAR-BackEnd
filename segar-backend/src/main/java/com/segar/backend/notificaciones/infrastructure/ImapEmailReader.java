package com.segar.backend.notificaciones.infrastructure;

import com.segar.backend.notificaciones.domain.*;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Implementación del puerto EmailReader usando IMAP
 */
@Component
@Slf4j
public class ImapEmailReader implements EmailReader {

    @Value("${spring.mail.imap.host}")
    private String imapHost;

    @Value("${spring.mail.imap.port}")
    private int imapPort;

    @Value("${spring.mail.imap.username}")
    private String username;

    @Value("${spring.mail.imap.password}")
    private String password;

    private Store store;

    @Override
    public List<Email> readInboxEmails() throws EmailReadingException {
        return readEmailsFromFolder("INBOX");
    }

    @Override
    public List<Email> readNewEmails() throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Buscar correos no leídos más recientes (últimos 30 días)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date thirtyDaysAgo = cal.getTime();

            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GE, thirtyDaysAgo);
            SearchTerm unreadTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SearchTerm combinedTerm = new AndTerm(dateTerm, unreadTerm);

            Message[] messages = inbox.search(combinedTerm);

            // Ordenar por fecha de recepción (más recientes primero)
            Arrays.sort(messages, (a, b) -> {
                try {
                    Date dateA = a.getReceivedDate();
                    Date dateB = b.getReceivedDate();
                    if (dateA == null && dateB == null) return 0;
                    if (dateA == null) return 1;
                    if (dateB == null) return -1;
                    return dateB.compareTo(dateA);
                } catch (MessagingException e) {
                    return 0;
                }
            });

            List<Email> emails = new ArrayList<>();
            for (Message message : messages) {
                try {
                    Email email = convertMessageToEmail(message);
                    emails.add(email);
                } catch (Exception e) {
                    log.warn("Error procesando mensaje: {}", e.getMessage());
                }
            }

            inbox.close(false);
            log.info("Se encontraron {} correos nuevos", emails.size());
            return emails;

        } catch (Exception e) {
            log.error("Error leyendo correos nuevos: {}", e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correos nuevos: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    @Override
    public List<Email> readEmailsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            SearchTerm startTerm = new ReceivedDateTerm(ComparisonTerm.GE, start);
            SearchTerm endTerm = new ReceivedDateTerm(ComparisonTerm.LE, end);
            SearchTerm combinedTerm = new AndTerm(startTerm, endTerm);

            Message[] messages = inbox.search(combinedTerm);

            List<Email> emails = new ArrayList<>();
            for (Message message : messages) {
                try {
                    Email email = convertMessageToEmail(message);
                    emails.add(email);
                } catch (Exception e) {
                    log.warn("Error procesando mensaje: {}", e.getMessage());
                }
            }

            inbox.close(false);
            return emails;

        } catch (Exception e) {
            log.error("Error leyendo correos por rango de fechas: {}", e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correos por rango de fechas: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    @Override
    public Email readEmailByMessageId(String messageId) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                Email email = convertMessageToEmail(messages[0]);
                inbox.close(false);
                return email;
            }

            inbox.close(false);
            throw new EmailReadingException("Correo no encontrado con Message-ID: " + messageId);

        } catch (Exception e) {
            log.error("Error leyendo correo por Message-ID: {}", e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correo por Message-ID: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    @Override
    public void markEmailAsRead(String messageId) throws EmailReadingException {
        updateEmailFlag(messageId, Flags.Flag.SEEN, true);
    }

    @Override
    public void markEmailAsUnread(String messageId) throws EmailReadingException {
        updateEmailFlag(messageId, Flags.Flag.SEEN, false);
    }

    @Override
    public void deleteEmailFromServer(String messageId) throws EmailReadingException {
        updateEmailFlag(messageId, Flags.Flag.DELETED, true);
    }

    @Override
    public boolean isConnected() {
        return store != null && store.isConnected();
    }

    @Override
    public void synchronizeEmails() throws EmailReadingException {
        log.info("Iniciando sincronización completa de correos");
        readNewEmails();
    }

    // Métodos privados mejorados

    private void connectToStore() throws MessagingException {
        if (store == null || !store.isConnected()) {
            Properties properties = new Properties();

            // Configuración IMAP específica para Gmail
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.ssl.enable", "true");
            properties.put("mail.imaps.ssl.trust", "*");
            properties.put("mail.imaps.host", imapHost);
            properties.put("mail.imaps.port", String.valueOf(imapPort));
            properties.put("mail.imaps.connectionpoolsize", "10");
            properties.put("mail.imaps.connectionpooltimeout", "300000");
            properties.put("mail.imaps.timeout", "300000");
            properties.put("mail.imaps.connectiontimeout", "300000");

            // Configuración adicional para Gmail
            properties.put("mail.imaps.ssl.protocols", "TLSv1.2");
            properties.put("mail.imaps.auth", "true");

            Session session = Session.getInstance(properties);
            store = session.getStore("imaps");
            store.connect(imapHost, username, password);
            log.info("Conectado exitosamente al servidor IMAP: {}", imapHost);
        }
    }

    private void closeStore() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
                log.debug("Conexión IMAP cerrada");
            }
        } catch (MessagingException e) {
            log.warn("Error cerrando conexión IMAP: {}", e.getMessage());
        }
    }

    private void updateEmailFlag(String messageId, Flags.Flag flag, boolean value) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                messages[0].setFlag(flag, value);
                log.info("Flag {} actualizada a {} para correo: {}", flag, value, messageId);
            } else {
                log.warn("No se encontró correo con Message-ID: {}", messageId);
            }

            inbox.close(true);

        } catch (Exception e) {
            log.error("Error actualizando flag del correo: {}", e.getMessage(), e);
            throw new EmailReadingException("Error actualizando correo: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    private List<Email> readEmailsFromFolder(String folderName) throws EmailReadingException {
        try {
            connectToStore();
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);

            // Obtener solo los últimos 50 correos para evitar sobrecarga
            Message[] allMessages = folder.getMessages();
            int totalCount = allMessages.length;
            int startIndex = Math.max(0, totalCount - 50);

            Message[] recentMessages = Arrays.copyOfRange(allMessages, startIndex, totalCount);

            List<Email> emails = new ArrayList<>();
            for (Message message : recentMessages) {
                try {
                    Email email = convertMessageToEmail(message);
                    emails.add(email);
                } catch (Exception e) {
                    log.warn("Error procesando mensaje: {}", e.getMessage());
                }
            }

            folder.close(false);
            log.info("Leídos {} correos de la carpeta {}", emails.size(), folderName);
            return emails;

        } catch (Exception e) {
            log.error("Error leyendo correos de la carpeta {}: {}", folderName, e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correos: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    private Email convertMessageToEmail(Message message) throws MessagingException, IOException {
        Email.EmailBuilder emailBuilder = Email.builder()
            .type(EmailType.INBOUND)
            .status(EmailStatus.RECEIVED)
            .isRead(message.isSet(Flags.Flag.SEEN))
            .receivedDate(convertToLocalDateTime(message.getReceivedDate()))
            .sentDate(convertToLocalDateTime(message.getSentDate()));

        // Configurar remitente
        if (message.getFrom() != null && message.getFrom().length > 0) {
            emailBuilder.fromAddress(message.getFrom()[0].toString());
        }

        // Configurar destinatarios
        if (message.getAllRecipients() != null) {
            emailBuilder.toAddresses(Arrays.toString(message.getAllRecipients()));
        }

        // Configurar asunto
        emailBuilder.subject(message.getSubject() != null ? message.getSubject() : "Sin asunto");

        // Obtener Message-ID
        String[] messageIds = message.getHeader("Message-ID");
        if (messageIds != null && messageIds.length > 0) {
            emailBuilder.messageId(messageIds[0]);
        }

        // Obtener In-Reply-To
        String[] inReplyToIds = message.getHeader("In-Reply-To");
        if (inReplyToIds != null && inReplyToIds.length > 0) {
            emailBuilder.inReplyTo(inReplyToIds[0]);
        }

        Email email = emailBuilder.build();

        // Procesar contenido
        processContent(message, email);

        return email;
    }

    private void processContent(Message message, Email email) throws MessagingException, IOException {
        try {
            if (message.isMimeType("text/plain")) {
                email.setContent(message.getContent().toString());
                email.setIsHtml(false);
            } else if (message.isMimeType("text/html")) {
                email.setContent(message.getContent().toString());
                email.setIsHtml(true);
            } else if (message.isMimeType("multipart/*")) {
                processMultipartContent((MimeMultipart) message.getContent(), email);
            } else {
                email.setContent("Contenido no soportado: " + message.getContentType());
                email.setIsHtml(false);
            }
        } catch (Exception e) {
            log.warn("Error procesando contenido del mensaje: {}", e.getMessage());
            email.setContent("Error procesando contenido: " + e.getMessage());
            email.setIsHtml(false);
        }
    }

    private void processMultipartContent(MimeMultipart multipart, Email email) throws MessagingException, IOException {
        StringBuilder textContent = new StringBuilder();
        StringBuilder htmlContent = new StringBuilder();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain") && textContent.length() == 0) {
                textContent.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("text/html") && htmlContent.length() == 0) {
                htmlContent.append(bodyPart.getContent().toString());
            } else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                       bodyPart.getFileName() != null) {
                processAttachment(bodyPart, email);
            }
        }

        // Preferir HTML si está disponible, sino texto plano
        if (htmlContent.length() > 0) {
            email.setContent(htmlContent.toString());
            email.setIsHtml(true);
        } else if (textContent.length() > 0) {
            email.setContent(textContent.toString());
            email.setIsHtml(false);
        } else {
            email.setContent("Contenido vacío");
            email.setIsHtml(false);
        }
    }

    private void processAttachment(BodyPart bodyPart, Email email) throws MessagingException, IOException {
        String fileName = bodyPart.getFileName();
        if (fileName != null) {
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                bodyPart.getInputStream().transferTo(buffer);

                EmailAttachment attachment = EmailAttachment.builder()
                    .email(email)
                    .fileName(fileName)
                    .contentType(bodyPart.getContentType())
                    .fileSize((long) buffer.size())
                    .fileContent(buffer.toByteArray())
                    .isInline(Part.INLINE.equalsIgnoreCase(bodyPart.getDisposition()))
                    .build();

                email.addAttachment(attachment);
                log.debug("Procesado archivo adjunto: {} ({} bytes)", fileName, buffer.size());
            }
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
