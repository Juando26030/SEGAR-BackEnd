package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.documentos.domain.SignedUrlRequest;
import com.segar.backend.documentos.service.DocumentService;







@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentosController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/signed-url")
    public String generateSignedUrl(@RequestBody SignedUrlRequest request) {

        String signedUrl = documentService.generateSignedUrl(
            request.getBucketName(),
            request.getObjectName(),
            request.getContentType()
        );

        
        String[] partes = request.getObjectName().split("/");

        String nombreEmpresa = partes[0];
        String nombreProducto = partes[1];
        String idDocumento = partes[2];
        String nombreArchivo = partes[3];

        documentService.saveDocumento(
            request.getBucketName(),
            request.getObjectName(),
            nombreEmpresa, 
            nombreProducto, 
            idDocumento, 
            nombreArchivo,
            request.getContentType()
        );

        return signedUrl;
    }

    @GetMapping("/all")
    public List<Documento> getAllDocs() {
        List<Documento> documentos = documentService.getAllDocumentos();

        return documentos;
    }
    
    @PostMapping("/get-signed-url")
    public String postMethodName(@RequestBody SignedUrlRequest request) {
        
        return documentService.generateGETSignedUrl(
            request.getBucketName(),
            request.getObjectName(),
            request.getContentType()
        );
    }
    
    @GetMapping("/tramite/{id}")
    public List<Documento> getDocumetsByTramiteId(@PathVariable("id") String id) {
        return documentService.getDocumentosByTramiteId(id);
    }
    

}
