package com.segar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO para crear o actualizar instancias de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentInstanceRequestDTO {

    @NotNull(message = "Template ID es obligatorio")
    private Long templateId;

    /**
     * Datos del formulario rellenado en formato JSON
     * Se valida contra el schema de la plantilla en el backend
     */
    private Map<String, Object> filledData;

    /**
     * Metadatos adicionales opcionales
     */
    private Map<String, Object> metadata;
}
