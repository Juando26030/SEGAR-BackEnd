package com.segar.backend.documentos.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public List<Documento> getDocumentosByTramiteId(String tramiteId_string) {
        Long tramiteId = Long.parseLong(tramiteId_string);
        return documentoRepository.findByTramiteId(tramiteId);
    }

    public void descargarYGuardar(String urlArchivo, String nombreArchivo, String idDocumento) throws IOException {

        // Carpeta donde se guardarán los archivos
        String directorioDestino = "data/files";

        // Crear carpeta si no existe
        Path carpeta = Paths.get(directorioDestino);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        // Crear nombre único del archivo usando idDocumento_nombreArchivo
        String nombreArchivoUnico = idDocumento + "_" + nombreArchivo;

        // Ruta final del archivo
        Path archivoDestino = carpeta.resolve(nombreArchivoUnico);

        // Abrir la conexión a la URL
        URL url = new URL(urlArchivo);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        try (InputStream inputStream = conexion.getInputStream()) {
            Files.copy(inputStream, archivoDestino, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Archivo descargado en: " + archivoDestino.toAbsolutePath());
    }

    public void descargarYGuardar(String urlArchivo, String nombreArchivoUnico) throws IOException {

        // Carpeta donde se guardarán los archivos
        String directorioDestino = "data/files";

        // Crear carpeta si no existe
        Path carpeta = Paths.get(directorioDestino);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        // Ruta final del archivo
        Path archivoDestino = carpeta.resolve(nombreArchivoUnico);

        // Abrir la conexión a la URL
        URL url = new URL(urlArchivo);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");

        try (InputStream inputStream = conexion.getInputStream()) {
            Files.copy(inputStream, archivoDestino, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Archivo descargado en: " + archivoDestino.toAbsolutePath());
    }

}
