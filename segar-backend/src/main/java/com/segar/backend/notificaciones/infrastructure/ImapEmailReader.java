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

            // Buscar correos no leídos
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

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
        }
    }

    @Override
    public List<Email> readEmailsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Convertir LocalDateTime a Date
            Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            // Buscar correos en el rango de fechas
            Message[] messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GE, start));
            messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.LE, end), messages);

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
        }
    }

    @Override
    public Email readEmailByMessageId(String messageId) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Buscar por Message-ID
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
        }
    }

    @Override
    public void markEmailAsRead(String messageId) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                messages[0].setFlag(Flags.Flag.SEEN, true);
                log.info("Correo marcado como leído: {}", messageId);
            }

            inbox.close(true);

        } catch (Exception e) {
            log.error("Error marcando correo como leído: {}", e.getMessage(), e);
            throw new EmailReadingException("Error marcando correo como leído: " + e.getMessage(), e);
        }
    }

    @Override
    public void markEmailAsUnread(String messageId) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                messages[0].setFlag(Flags.Flag.SEEN, false);
                log.info("Correo marcado como no leído: {}", messageId);
            }

            inbox.close(true);

        } catch (Exception e) {
            log.error("Error marcando correo como no leído: {}", e.getMessage(), e);
            throw new EmailReadingException("Error marcando correo como no leído: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteEmailFromServer(String messageId) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            SearchTerm searchTerm = new HeaderTerm("Message-ID", messageId);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                messages[0].setFlag(Flags.Flag.DELETED, true);
                log.info("Correo eliminado del servidor: {}", messageId);
            }

            inbox.close(true);

        } catch (Exception e) {
            log.error("Error eliminando correo del servidor: {}", e.getMessage(), e);
            throw new EmailReadingException("Error eliminando correo del servidor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isConnected() {
        return store != null && store.isConnected();
    }

    @Override
    public void synchronizeEmails() throws EmailReadingException {
        readNewEmails(); // Por simplicidad, usa la misma lógica que readNewEmails
    }

    // Métodos privados de utilidad

    private void connectToStore() throws MessagingException {
        if (store == null || !store.isConnected()) {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.ssl.enable", "true");
            properties.put("mail.imaps.host", imapHost);
            properties.put("mail.imaps.port", imapPort);

            Session session = Session.getInstance(properties);
            store = session.getStore("imaps");
            store.connect(imapHost, username, password);
            log.debug("Conectado al servidor IMAP: {}", imapHost);
        }
    }

    private List<Email> readEmailsFromFolder(String folderName) throws EmailReadingException {
        try {
            connectToStore();
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            List<Email> emails = new ArrayList<>();

            for (Message message : messages) {
                try {
                    Email email = convertMessageToEmail(message);
                    emails.add(email);
                } catch (Exception e) {
                    log.warn("Error procesando mensaje: {}", e.getMessage());
                }
            }

            folder.close(false);
            return emails;

        } catch (Exception e) {
            log.error("Error leyendo correos de la carpeta {}: {}", folderName, e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correos: " + e.getMessage(), e);
        }
    }

    private Email convertMessageToEmail(Message message) throws MessagingException, IOException {
        Email email = Email.builder()
            .fromAddress(message.getFrom()[0].toString())
            .toAddresses(Arrays.toString(message.getAllRecipients()))
            .subject(message.getSubject())
            .isRead(message.isSet(Flags.Flag.SEEN))
            .type(EmailType.INBOUND)
            .status(EmailStatus.RECEIVED)
            .receivedDate(convertToLocalDateTime(message.getReceivedDate()))
            .sentDate(convertToLocalDateTime(message.getSentDate()))
            .build();

        // Obtener Message-ID si está disponible
        String[] messageIds = message.getHeader("Message-ID");
        if (messageIds != null && messageIds.length > 0) {
            email.setMessageId(messageIds[0]);
        }

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
            email.setContent("Error procesando contenido");
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
                // Procesar archivo adjunto
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
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
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
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
