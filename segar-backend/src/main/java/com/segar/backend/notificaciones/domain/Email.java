package com.segar.backend.notificaciones.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa un correo electrónico
 */
@Entity
@Table(name = "emails")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String toAddresses;

    @Column(columnDefinition = "TEXT")
    private String ccAddresses;

    @Column(columnDefinition = "TEXT")
    private String bccAddresses;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Boolean isHtml;

    @Column
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    @Enumerated(EnumType.STRING)
    private EmailType type;

    @Column
    private LocalDateTime sentDate;

    @Column
    private LocalDateTime receivedDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmailAttachment> attachments = new ArrayList<>();

    @Column
    private String messageId;

    @Column
    private String inReplyTo;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
        if (isHtml == null) {
            isHtml = false;
        }
        if (status == null) {
            status = EmailStatus.DRAFT;
        }
        if (type == null) {
            type = EmailType.OUTBOUND;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.isRead = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void addAttachment(EmailAttachment attachment) {
        attachments.add(attachment);
        attachment.setEmail(this);
    }

    public void removeAttachment(EmailAttachment attachment) {
        attachments.remove(attachment);
        attachment.setEmail(null);
    }
}
