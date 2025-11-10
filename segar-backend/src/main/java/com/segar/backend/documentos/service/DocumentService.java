package com.segar.backend.documentos.service;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.segar.backend.documentos.domain.Documento;
import com.segar.backend.documentos.infrastructure.DocumentoRepository;
import com.segar.backend.shared.domain.Tramite;
import com.segar.backend.shared.infrastructure.TramiteRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DocumentService {

    @Autowired
    private TramiteRepository tramiteRepository;

    @Autowired
    private DocumentoRepository documentoRepository;


    private final Storage storage;
    
    public String generateSignedUrl(String bucketName, String objectName, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
            .setContentType(contentType)
            .build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withContentType()
        );

        return signedUrl.toString();
    }

    public void saveDocumento(String bucketName, String objectName, String nombreEmpresa, String nombreProducto, String idDocumento, String nombreArchivo, String contentType) {

        Documento documento = new Documento(bucketName, objectName, nombreEmpresa, nombreProducto, idDocumento, nombreArchivo, contentType);

        Tramite tramite = tramiteRepository.findByNombreProducto(nombreProducto).get();

        tramite.addDocumento(documento);
        documento.setTramite(tramite);

        tramiteRepository.save(tramite);
    }

    public List<Documento> getAllDocumentos() {
        return documentoRepository.findAll();
    }

    public String generateGETSignedUrl(String bucketName, String objectName, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET)
        );

        return signedUrl.toString();
    }
}
