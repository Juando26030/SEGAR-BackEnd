package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.segar.backend.documentos.api.DocumentosController.SignedUrlRequest;
import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.documentos.infrastructure.DocumentoRepository;
import com.segar.backend.shared.domain.Tramite;
import com.segar.backend.shared.infrastructure.TramiteRepository;

import java.net.URL;



@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentosController {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private TramiteRepository tramiteRepository;

    private final Storage storage;

    @PostMapping("/signed-url")
    public String generateSignedUrl(@RequestBody SignedUrlRequest request) {
        BlobInfo blobInfo = BlobInfo.newBuilder(request.getBucketName(), request.getObjectName())
            .setContentType(request.getContentType())
            .build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withContentType()
        );

        Documento documento = new Documento(request.getBucketName(), request.getObjectName(), request.getContentType());
        String[] partes = request.getObjectName().split("/");

        String nombreProducto = partes[1];

        Tramite tramite = tramiteRepository.findByNombreProducto(nombreProducto).get();

        System.out.println("Tramite encontrado: " + tramite.getId());
        tramite.addDocumento(documento);
        documento.setTramite(tramite);

        tramiteRepository.save(tramite);
        documentoRepository.save(documento);


        return signedUrl.toString();
    }

    public static class SignedUrlRequest {
        private String bucketName;
        private String objectName;
        private String contentType;

        public String getBucketName() { return bucketName; }
        public void setBucketName(String bucketName) { this.bucketName = bucketName; }
        public String getObjectName() { return objectName; }
        public void setObjectName(String objectName) { this.objectName = objectName; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }
}
