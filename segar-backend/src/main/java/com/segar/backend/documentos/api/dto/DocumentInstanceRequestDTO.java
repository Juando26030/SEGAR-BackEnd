package com.segar.backend.documentos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para solicitudes de creación/actualización de instancias de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentInstanceRequestDTO {

    private Long templateId;
    private Map<String, Object> filledData;
    private Map<String, Object> metadata;
}
