package com.segar.backend.notificaciones.api.dto;

import com.segar.backend.notificaciones.domain.EmailStatus;
import com.segar.backend.notificaciones.domain.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de correos electrónicos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private Long id;
    private String fromAddress;
    private List<String> toAddresses;
    private List<String> ccAddresses;
    private List<String> bccAddresses;
    private String subject;
    private String content;
    private Boolean isHtml;
    private Boolean isRead;
    private EmailStatus status;
    private EmailType type;
    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EmailAttachmentDto> attachments;
    private String messageId;
    private String inReplyTo;
    private String errorMessage;
    private Integer attachmentCount;
}
