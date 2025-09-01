package com.segar.backend.services;

import com.segar.backend.dto.DocumentInstanceDTO;
import com.segar.backend.dto.DocumentInstanceRequestDTO;
import com.segar.backend.dto.DocumentExportRequestDTO;
import com.segar.backend.dto.DocumentExportResponseDTO;
import com.segar.backend.dto.DocumentCompletionSummaryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Servicio para gestión de instancias de documentos
 */
public interface DocumentInstanceService {

    /**
     * Obtiene instancias de documentos por trámite
     */
    List<DocumentInstanceDTO> getInstancesByTramite(Long tramiteId);

    /**
     * Obtiene una instancia específica por ID
     */
    DocumentInstanceDTO getInstanceById(Long id);

    /**
     * Crea una nueva instancia de documento
     */
    DocumentInstanceDTO createInstance(Long tramiteId, DocumentInstanceRequestDTO requestDTO);

    /**
     * Actualiza una instancia existente
     */
    DocumentInstanceDTO updateInstance(Long tramiteId, Long instanceId, DocumentInstanceRequestDTO requestDTO);

    /**
     * Sube archivo(s) a una instancia
     */
    DocumentInstanceDTO uploadFiles(Long tramiteId, Long instanceId, MultipartFile[] files);

    /**
     * Exporta una instancia a PDF
     */
    DocumentExportResponseDTO exportToPdf(Long tramiteId, Long instanceId, DocumentExportRequestDTO requestDTO);

    /**
     * Elimina una instancia
     */
    void deleteInstance(Long tramiteId, Long instanceId);

    /**
     * Verifica si todos los documentos obligatorios están completados para un trámite
     */
    boolean areAllRequiredDocumentsCompleted(Long tramiteId);

    /**
     * Obtiene el resumen de completitud de documentos para un trámite
     */
    DocumentCompletionSummaryDTO getDocumentCompletionSummary(Long tramiteId);

    /**
     * Valida los datos de un formulario contra su plantilla
     */
    boolean validateInstanceData(Long templateId, String filledData);
}
