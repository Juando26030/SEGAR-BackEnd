package com.segar.backend.services;

import com.segar.backend.models.DocumentInstance;
import com.segar.backend.dto.DocumentExportRequestDTO;
import com.segar.backend.dto.DocumentExportResponseDTO;

/**
 * Servicio para generar PDFs a partir de plantillas y datos de documentos
 */
public interface DocumentPdfService {

    /**
     * Genera un PDF a partir de una instancia de documento
     */
    DocumentExportResponseDTO generatePdf(DocumentInstance instance, DocumentExportRequestDTO request) throws Exception;

    /**
     * Genera un PDF combinado con múltiples documentos
     */
    DocumentExportResponseDTO generateCombinedPdf(Long tramiteId, DocumentExportRequestDTO request) throws Exception;

    /**
     * Verifica si el servicio está disponible y configurado correctamente
     */
    boolean isAvailable();
}
