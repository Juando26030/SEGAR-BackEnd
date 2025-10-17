package com.segar.backend.notificaciones.domain;

import com.segar.backend.notificaciones.api.dto.EmailSearchFilter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto para la lectura de correos electrónicos desde el servidor
 */
public interface EmailReader {

    /**
     * Lee todos los correos del buzón de entrada
     */
    List<Email> readInboxEmails() throws EmailReadingException;

    /**
     * Lee correos nuevos (no leídos) del buzón de entrada
     */
    List<Email> readNewEmails() throws EmailReadingException;

    /**
     * Lee correos enviados desde el servidor IMAP (carpeta Sent Mail)
     */
    List<Email> readSentEmails() throws EmailReadingException;

    /**
     * Lee correos con filtros específicos desde el servidor IMAP
     */
    List<Email> readEmailsWithFilters(EmailSearchFilter filter) throws EmailReadingException;

    /**
     * Lee correos en un rango de fechas específico
     */
    List<Email> readEmailsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws EmailReadingException;

    /**
     * Lee un correo específico por su ID de mensaje
     */
    Email readEmailByMessageId(String messageId) throws EmailReadingException;

    /**
     * Marca un correo como leído en el servidor
     */
    void markEmailAsRead(String messageId) throws EmailReadingException;

    /**
     * Marca un correo como no leído en el servidor
     */
    void markEmailAsUnread(String messageId) throws EmailReadingException;

    /**
     * Elimina un correo del servidor
     */
    void deleteEmailFromServer(String messageId) throws EmailReadingException;

    /**
     * Verifica si hay conexión con el servidor de correo
     */
    boolean isConnected();

    /**
     * Sincroniza correos locales con el servidor
     */
    void synchronizeEmails() throws EmailReadingException;
}
