package com.segar.backend.notificaciones.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para archivos adjuntos de correos electrónicos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachmentDto {

    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String contentId;
    private Boolean isInline;
    private LocalDateTime createdAt;
    private String downloadUrl;

    // Métodos de conveniencia
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    public boolean isDocument() {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.equals("application/msword") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
            contentType.equals("text/plain")
        );
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";

        long bytes = fileSize;
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}
