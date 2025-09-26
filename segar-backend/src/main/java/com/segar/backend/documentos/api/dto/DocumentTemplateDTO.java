package com.segar.backend.documentos.api.dto;

import com.segar.backend.shared.domain.TipoTramite;
import com.segar.backend.shared.domain.CategoriaRiesgo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para transferencia de datos de plantillas de documentos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplateDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String fieldsDefinition;
    private String fileRules;
    private Set<TipoTramite> appliesToTramiteTypes;
    private Integer version;
    private Boolean active;
    private Boolean required;
    private Integer displayOrder;
    private CategoriaRiesgo categoriaRiesgo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
