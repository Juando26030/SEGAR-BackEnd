package com.segar.backend.notificaciones.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DTO para envío de correos electrónicos
 * Todos los campos son opcionales excepto toAddresses, subject y content
 */
@Data
public class SendEmailRequest {

    @NotEmpty(message = "Debe especificar al menos un destinatario")
    private List<@Email(message = "Formato de email inválido") String> toAddresses;

    // Campos opcionales para copias
    private List<@Email(message = "Formato de email inválido") String> ccAddresses;
    private List<@Email(message = "Formato de email inválido") String> bccAddresses;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    // Opcional: Por defecto es texto plano (false)
    private Boolean isHtml = false;

    // Opcional: Archivos adjuntos
    private List<MultipartFile> attachments;

    // Opcional: Archivos inline (para embeber en HTML)
    private List<MultipartFile> inlineFiles;

    // Opcional: Nombres personalizados para destinatarios
    private List<String> toNames;
    private List<String> ccNames;
    private List<String> bccNames;
}
