package com.segar.backend.notificaciones.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DTO para envío de correos electrónicos
 */
@Data
public class SendEmailRequest {

    @NotEmpty(message = "Debe especificar al menos un destinatario")
    private List<@Email(message = "Formato de email inválido") String> toAddresses;

    private List<@Email(message = "Formato de email inválido") String> ccAddresses;

    private List<@Email(message = "Formato de email inválido") String> bccAddresses;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private Boolean isHtml = false;

    private List<MultipartFile> attachments;

    private List<MultipartFile> inlineFiles;

    // Nombres opcionales para los destinatarios
    private List<String> toNames;
    private List<String> ccNames;
    private List<String> bccNames;
}
