package com.segar.backend.notificaciones.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * Value Object que representa el contenido de un correo electrónico
 */
@Value
public class EmailContent {

    @NotBlank(message = "El asunto del correo no puede estar vacío")
    String subject;

    @NotBlank(message = "El contenido del correo no puede estar vacío")
    String body;

    boolean isHtml;

    public EmailContent(String subject, String body) {
        this(subject, body, false);
    }

    public EmailContent(String subject, String body, boolean isHtml) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("El asunto del correo no puede estar vacío");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del correo no puede estar vacío");
        }

        this.subject = subject.trim();
        this.body = body.trim();
        this.isHtml = isHtml;
    }

    public String getPlainTextBody() {
        if (!isHtml) {
            return body;
        }
        // Conversión básica de HTML a texto plano
        return body.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
    }

    public int getBodyLength() {
        return body.length();
    }

    public boolean isEmpty() {
        return subject.isEmpty() && body.isEmpty();
    }
}
