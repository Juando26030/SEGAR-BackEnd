package com.segar.backend.notificaciones.infrastructure;

import com.segar.backend.notificaciones.api.dto.EmailSearchFilter;
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

            // Buscar correos más recientes (últimos 30 días) - TODOS, no solo no leídos
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date thirtyDaysAgo = cal.getTime();

            SearchTerm dateTerm = new ReceivedDateTerm(ComparisonTerm.GE, thirtyDaysAgo);
            Message[] messages = inbox.search(dateTerm);

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
            log.info("Se encontraron {} correos (todos, no solo no leídos)", emails.size());
            return emails;

        } catch (Exception e) {
            log.error("Error leyendo correos nuevos: {}", e.getMessage(), e);
            throw new EmailReadingException("Error leyendo correos nuevos: " + e.getMessage(), e);
        } finally {
            closeStore();
        }
    }

    /**
     * Lee correos con filtros específicos desde el servidor IMAP
     */
    public List<Email> readEmailsWithFilters(EmailSearchFilter filter) throws EmailReadingException {
        try {
            connectToStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Construir términos de búsqueda
            List<SearchTerm> searchTerms = new ArrayList<>();

            // Filtro por fecha
            if (filter.getStartDate() != null) {
                Date startDate = Date.from(filter.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
                searchTerms.add(new ReceivedDateTerm(ComparisonTerm.GE, startDate));
            }
            if (filter.getEndDate() != null) {
                Date endDate = Date.from(filter.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
                searchTerms.add(new ReceivedDateTerm(ComparisonTerm.LE, endDate));
            }

            // Filtro por remitente
            if (filter.getFromAddress() != null && !filter.getFromAddress().trim().isEmpty()) {
                searchTerms.add(new FromStringTerm(filter.getFromAddress()));
            }

            // Filtro por asunto
            if (filter.getSubject() != null && !filter.getSubject().trim().isEmpty()) {
                searchTerms.add(new SubjectTerm(filter.getSubject()));
            }

            // Filtro por estado de lectura
            if (filter.getIsRead() != null) {
                Flags.Flag seenFlag = Flags.Flag.SEEN;
                searchTerms.add(new FlagTerm(new Flags(seenFlag), filter.getIsRead()));
            }

            // Búsqueda de texto general
            if (filter.getSearchText() != null && !filter.getSearchText().trim().isEmpty()) {
                String searchText = filter.getSearchText().trim();
                // Buscar en asunto O contenido OR remitente
                SearchTerm subjectSearch = new SubjectTerm(searchText);
                SearchTerm fromSearch = new FromStringTerm(searchText);
                SearchTerm bodySearch = new BodyTerm(searchText);

                SearchTerm textSearch = new OrTerm(new OrTerm(subjectSearch, fromSearch), bodySearch);
                searchTerms.add(textSearch);
            }

            // Combinar todos los términos con AND
            SearchTerm combinedTerm = null;
            if (!searchTerms.isEmpty()) {
                combinedTerm = searchTerms.get(0);
                for (int i = 1; i < searchTerms.size(); i++) {
                    combinedTerm = new AndTerm(combinedTerm, searchTerms.get(i));
                }
            }

            // Buscar mensajes
            Message[] messages;
            if (combinedTerm != null) {
                messages = inbox.search(combinedTerm);
            } else {
                // Si no hay filtros, obtener todos los mensajes recientes
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -30);
                Date thirtyDaysAgo = cal.getTime();
                messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GE, thirtyDaysAgo));
            }

            // Ordenar por fecha
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
            log.info("Búsqueda con filtros completada. {} correos encontrados", emails.size());
            return emails;

        } catch (Exception e) {
            log.error("Error buscando correos con filtros: {}", e.getMessage(), e);
            throw new EmailReadingException("Error buscando correos: " + e.getMessage(), e);
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
            log.debug("Procesando contenido del mensaje. Content-Type: {}", message.getContentType());

            if (message.isMimeType("text/plain")) {
                String content = extractTextContent(message);
                email.setContent(content);
                email.setIsHtml(false);
                log.debug("Contenido texto extraído: {} caracteres", content.length());
            } else if (message.isMimeType("text/html")) {
                String content = extractTextContent(message);
                email.setContent(content);
                email.setIsHtml(true);
                log.debug("Contenido HTML extraído: {} caracteres", content.length());
            } else if (message.isMimeType("multipart/*")) {
                processMultipartContent((MimeMultipart) message.getContent(), email);
            } else {
                // Intentar extraer contenido como string por defecto
                String fallbackContent = extractFallbackContent(message);
                email.setContent(fallbackContent);
                email.setIsHtml(false);
                log.debug("Contenido fallback extraído: {} caracteres", fallbackContent.length());
            }
        } catch (Exception e) {
            log.error("Error procesando contenido del mensaje: {}", e.getMessage(), e);
            email.setContent("Error al procesar contenido: " + e.getMessage());
            email.setIsHtml(false);
        }
    }

    private String extractTextContent(Message message) throws MessagingException, IOException {
        try {
            Object content = message.getContent();
            if (content == null) {
                return "Sin contenido disponible";
            }

            String textContent = content.toString();
            if (textContent == null || textContent.trim().isEmpty()) {
                return "Contenido sin texto";
            }

            return textContent;
        } catch (Exception e) {
            log.warn("Error extrayendo contenido de texto: {}", e.getMessage());
            return "Error extrayendo contenido";
        }
    }

    private String extractFallbackContent(Message message) throws MessagingException {
        try {
            // Intentar obtener contenido usando diferentes métodos
            Object content = message.getContent();
            if (content != null) {
                return content.toString();
            }

            // Si no hay contenido, intentar con headers
            String[] subjects = message.getHeader("Subject");
            if (subjects != null && subjects.length > 0) {
                return "Mensaje con asunto: " + subjects[0];
            }

            return "Mensaje sin contenido disponible";
        } catch (Exception e) {
            log.warn("Error en extracción fallback: {}", e.getMessage());
            return "Contenido no disponible";
        }
    }

    private void processMultipartContent(MimeMultipart multipart, Email email) throws MessagingException, IOException {
        StringBuilder textContent = new StringBuilder();
        StringBuilder htmlContent = new StringBuilder();
        boolean foundContent = false;

        log.debug("Procesando multipart con {} partes", multipart.getCount());

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType();
            String disposition = bodyPart.getDisposition();

            log.debug("Parte {}: ContentType={}, Disposition={}", i, contentType, disposition);

            // Procesar contenido de texto/HTML - SER MÁS AGRESIVO
            if (bodyPart.isMimeType("text/plain")) {
                try {
                    Object content = bodyPart.getContent();
                    if (content != null) {
                        String textPart = content.toString().trim();
                        if (!textPart.isEmpty()) {
                            if (textContent.length() > 0) {
                                textContent.append("\n");
                            }
                            textContent.append(textPart);
                            foundContent = true;
                            log.debug("Contenido texto encontrado en parte {}: '{}' ({} caracteres)",
                                i, textPart.substring(0, Math.min(50, textPart.length())), textPart.length());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error extrayendo texto de parte {}: {}", i, e.getMessage());
                }
            } else if (bodyPart.isMimeType("text/html")) {
                try {
                    Object content = bodyPart.getContent();
                    if (content != null) {
                        String htmlPart = content.toString().trim();
                        if (!htmlPart.isEmpty()) {
                            if (htmlContent.length() > 0) {
                                htmlContent.append("\n");
                            }
                            htmlContent.append(htmlPart);
                            foundContent = true;
                            log.debug("Contenido HTML encontrado en parte {}: '{}' ({} caracteres)",
                                i, htmlPart.substring(0, Math.min(50, htmlPart.length())), htmlPart.length());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error extrayendo HTML de parte {}: {}", i, e.getMessage());
                }
            }
            // Intentar extraer contenido de cualquier parte que no sea adjunto
            else if (!isAttachment(bodyPart)) {
                try {
                    Object content = bodyPart.getContent();
                    if (content != null) {
                        String anyContent = content.toString().trim();
                        if (!anyContent.isEmpty() && anyContent.length() > 5) { // Filtrar contenido muy corto
                            if (textContent.length() > 0) {
                                textContent.append("\n");
                            }
                            textContent.append(anyContent);
                            foundContent = true;
                            log.debug("Contenido genérico encontrado en parte {}: '{}' ({} caracteres)",
                                i, anyContent.substring(0, Math.min(50, anyContent.length())), anyContent.length());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error extrayendo contenido genérico de parte {}: {}", i, e.getMessage());
                }
            }

            // Procesar adjuntos - mejorar detección
            if (isAttachment(bodyPart)) {
                try {
                    processAttachment(bodyPart, email);
                } catch (Exception e) {
                    log.error("Error procesando adjunto en parte {}: {}", i, e.getMessage(), e);
                }
            }

            // Si la parte es multipart anidado, procesarlo recursivamente
            if (bodyPart.isMimeType("multipart/*")) {
                try {
                    MimeMultipart nestedMultipart = (MimeMultipart) bodyPart.getContent();
                    Email tempEmail = new Email();
                    processMultipartContent(nestedMultipart, tempEmail);

                    if (tempEmail.getContent() != null && !tempEmail.getContent().trim().isEmpty()) {
                        String nestedContent = tempEmail.getContent().trim();
                        if (tempEmail.getIsHtml()) {
                            if (htmlContent.length() > 0) {
                                htmlContent.append("\n");
                            }
                            htmlContent.append(nestedContent);
                        } else {
                            if (textContent.length() > 0) {
                                textContent.append("\n");
                            }
                            textContent.append(nestedContent);
                        }
                        foundContent = true;
                        log.debug("Contenido multipart anidado encontrado: '{}' ({} caracteres)",
                            nestedContent.substring(0, Math.min(50, nestedContent.length())), nestedContent.length());
                    }
                } catch (Exception e) {
                    log.warn("Error procesando multipart anidado en parte {}: {}", i, e.getMessage());
                }
            }
        }

        // Asignar contenido con prioridad HTML > texto > fallback
        String finalContent = "";
        boolean isHtml = false;

        if (htmlContent.length() > 0) {
            finalContent = htmlContent.toString().trim();
            isHtml = true;
            log.debug("Asignado contenido HTML final: {} caracteres", finalContent.length());
        } else if (textContent.length() > 0) {
            finalContent = textContent.toString().trim();
            isHtml = false;
            log.debug("Asignado contenido texto final: {} caracteres", finalContent.length());
        }

        // Si encontramos contenido, asignarlo
        if (!finalContent.isEmpty()) {
            email.setContent(finalContent);
            email.setIsHtml(isHtml);
            log.info("Contenido multipart extraído exitosamente: '{}' ({} caracteres, HTML: {})",
                finalContent.substring(0, Math.min(100, finalContent.length())), finalContent.length(), isHtml);
        } else {
            // Último intento: buscar cualquier texto en cualquier parte
            String fallbackContent = extractFallbackFromMultipart(multipart);
            if (!fallbackContent.isEmpty()) {
                email.setContent(fallbackContent);
                email.setIsHtml(false);
                log.info("Contenido fallback extraído: '{}' ({} caracteres)",
                    fallbackContent.substring(0, Math.min(100, fallbackContent.length())), fallbackContent.length());
            } else {
                email.setContent("Mensaje multipart sin contenido de texto disponible");
                email.setIsHtml(false);
                log.warn("No se pudo encontrar contenido en mensaje multipart con {} partes", multipart.getCount());
            }
        }
    }

    private String extractFallbackFromMultipart(MimeMultipart multipart) throws MessagingException {
        StringBuilder fallback = new StringBuilder();

        try {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);

                try {
                    // Intentar obtener CUALQUIER contenido como string
                    Object content = bodyPart.getContent();
                    if (content != null) {
                        String stringContent = content.toString();
                        if (stringContent != null && stringContent.trim().length() > 0) {
                            // Filtrar contenido que parece ser headers o metadata
                            if (!stringContent.contains("Content-Type:") &&
                                !stringContent.contains("Content-Transfer-Encoding:") &&
                                !stringContent.startsWith("--") &&
                                stringContent.length() > 2) {

                                if (fallback.length() > 0) {
                                    fallback.append("\n");
                                }
                                fallback.append(stringContent.trim());
                                log.debug("Fallback: encontrado texto en parte {}: '{}'",
                                    i, stringContent.substring(0, Math.min(30, stringContent.length())));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("No se pudo extraer contenido fallback de parte {}: {}", i, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Error en extracción fallback completa: {}", e.getMessage());
        }

        return fallback.toString().trim();
    }

    private boolean isAttachment(BodyPart bodyPart) throws MessagingException {
        String disposition = bodyPart.getDisposition();
        String fileName = bodyPart.getFileName();

        // Es adjunto si tiene disposition ATTACHMENT
        if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
            return true;
        }

        // Es adjunto si tiene filename (incluso sin disposition)
        if (fileName != null && !fileName.trim().isEmpty()) {
            return true;
        }

        // Es adjunto si no es texto/html y tiene content-type específico
        String contentType = bodyPart.getContentType();
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (!lowerContentType.startsWith("text/") &&
                !lowerContentType.startsWith("multipart/") &&
                (lowerContentType.contains("application/") ||
                 lowerContentType.contains("image/") ||
                 lowerContentType.contains("audio/") ||
                 lowerContentType.contains("video/"))) {
                return true;
            }
        }

        return false;
    }

    private void processAttachment(BodyPart bodyPart, Email email) throws MessagingException, IOException {
        log.debug("Procesando adjunto...");

        // Extraer filename con múltiples métodos
        String fileName = extractFileName(bodyPart);
        String contentType = bodyPart.getContentType();

        // Extraer tamaño real del archivo
        long fileSize = extractFileSize(bodyPart);

        // Extraer contenido del archivo
        byte[] fileContent = null;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            bodyPart.getInputStream().transferTo(buffer);
            fileContent = buffer.toByteArray();

            // Si no pudimos obtener el tamaño antes, usar el buffer
            if (fileSize <= 0) {
                fileSize = fileContent.length;
            }
        }

        // Determinar si es inline
        boolean isInline = Part.INLINE.equalsIgnoreCase(bodyPart.getDisposition());

        EmailAttachment attachment = EmailAttachment.builder()
            .email(email)
            .fileName(fileName)
            .contentType(contentType)
            .fileSize(fileSize)
            .fileContent(fileContent)
            .isInline(isInline)
            .build();

        email.addAttachment(attachment);
        log.info("Adjunto procesado exitosamente: {} ({} bytes, {})", fileName, fileSize, contentType);
    }

    private String extractFileName(BodyPart bodyPart) throws MessagingException {
        // Método 1: getFileName() directo
        String fileName = bodyPart.getFileName();
        if (fileName != null && !fileName.trim().isEmpty() && !"unknown".equalsIgnoreCase(fileName)) {
            return cleanFileName(fileName);
        }

        // Método 2: Extraer de Content-Disposition header
        String[] dispositionHeaders = bodyPart.getHeader("Content-Disposition");
        if (dispositionHeaders != null && dispositionHeaders.length > 0) {
            String extractedName = extractFilenameFromDisposition(dispositionHeaders[0]);
            if (extractedName != null) {
                return cleanFileName(extractedName);
            }
        }

        // Método 3: Extraer de Content-Type header
        String contentType = bodyPart.getContentType();
        if (contentType != null && contentType.contains("name=")) {
            String extractedName = extractFilenameFromContentType(contentType);
            if (extractedName != null) {
                return cleanFileName(extractedName);
            }
        }

        // Método 4: Generar nombre basado en Content-Type
        return generateFileNameFromContentType(contentType);
    }

    private String extractFilenameFromDisposition(String disposition) {
        if (disposition == null) return null;

        // Buscar filename= o filename*=
        String[] patterns = {"filename=\"", "filename=", "filename*=UTF-8''", "filename*="};

        for (String pattern : patterns) {
            int startIndex = disposition.indexOf(pattern);
            if (startIndex != -1) {
                startIndex += pattern.length();
                int endIndex = disposition.indexOf(";", startIndex);
                if (endIndex == -1) endIndex = disposition.length();

                String filename = disposition.substring(startIndex, endIndex).trim();
                if (filename.startsWith("\"") && filename.endsWith("\"")) {
                    filename = filename.substring(1, filename.length() - 1);
                }
                return filename;
            }
        }
        return null;
    }

    private String extractFilenameFromContentType(String contentType) {
        if (contentType == null) return null;

        int nameIndex = contentType.indexOf("name=");
        if (nameIndex != -1) {
            nameIndex += 5; // "name=".length()
            int endIndex = contentType.indexOf(";", nameIndex);
            if (endIndex == -1) endIndex = contentType.length();

            String filename = contentType.substring(nameIndex, endIndex).trim();
            if (filename.startsWith("\"") && filename.endsWith("\"")) {
                filename = filename.substring(1, filename.length() - 1);
            }
            return filename;
        }
        return null;
    }

    private String generateFileNameFromContentType(String contentType) {
        if (contentType == null) return "archivo_adjunto";

        String lowerContentType = contentType.toLowerCase();

        if (lowerContentType.contains("pdf")) return "documento.pdf";
        if (lowerContentType.contains("word") || lowerContentType.contains("msword")) return "documento.doc";
        if (lowerContentType.contains("excel") || lowerContentType.contains("spreadsheet")) return "hoja_calculo.xlsx";
        if (lowerContentType.contains("powerpoint") || lowerContentType.contains("presentation")) return "presentacion.ppt";
        if (lowerContentType.contains("image/jpeg") || lowerContentType.contains("image/jpg")) return "imagen.jpg";
        if (lowerContentType.contains("image/png")) return "imagen.png";
        if (lowerContentType.contains("image/gif")) return "imagen.gif";
        if (lowerContentType.contains("text/plain")) return "archivo.txt";
        if (lowerContentType.contains("text/html")) return "pagina.html";
        if (lowerContentType.contains("application/zip")) return "archivo.zip";

        return "archivo_adjunto";
    }

    private String cleanFileName(String fileName) {
        if (fileName == null) return "archivo_adjunto";

        // Remover caracteres problemáticos
        String cleaned = fileName.replaceAll("[\\r\\n\\t]", "").trim();

        // Si está vacío después de limpiar
        if (cleaned.isEmpty()) {
            return "archivo_adjunto";
        }

        return cleaned;
    }

    private long extractFileSize(BodyPart bodyPart) throws MessagingException {
        // Método 1: getSize() directo
        int size = bodyPart.getSize();
        if (size > 0) {
            return size;
        }

        // Método 2: Content-Length header
        String[] lengthHeaders = bodyPart.getHeader("Content-Length");
        if (lengthHeaders != null && lengthHeaders.length > 0) {
            try {
                return Long.parseLong(lengthHeaders[0]);
            } catch (NumberFormatException e) {
                log.warn("No se pudo parsear Content-Length: {}", lengthHeaders[0]);
            }
        }

        // Método 3: Leer el contenido para obtener el tamaño (como último recurso)
        try (ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream()) {
            bodyPart.getInputStream().transferTo(tempBuffer);
            return tempBuffer.size();
        } catch (IOException e) {
            log.warn("No se pudo obtener tamaño del adjunto: {}", e.getMessage());
            return 0;
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
