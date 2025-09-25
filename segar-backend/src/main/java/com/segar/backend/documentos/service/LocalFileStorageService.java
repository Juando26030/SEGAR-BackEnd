package com.segar.backend.documentos.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementación de almacenamiento local para archivos
 * Almacena archivos en el sistema de archivos local
 */
@Service
@Slf4j
public class LocalFileStorageService {

    @Value("${app.storage.local.base-path:uploads}")
    private String basePath;

    @Value("${app.storage.local.url-prefix:http://localhost:8080/api/files}")
    private String urlPrefix;

     
    public String store(MultipartFile file, String folder) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storageKey = folder + "/" + UUID.randomUUID().toString() + extension;

        Path targetPath = Paths.get(basePath, storageKey);

        // Crear directorios si no existen
        Files.createDirectories(targetPath.getParent());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath);
        }

        log.info("Archivo almacenado: {} -> {}", originalFilename, storageKey);
        return storageKey;
    }

     
    public String store(InputStream inputStream, String fileName, String folder, String contentType) throws Exception {
        String extension = getFileExtension(fileName);
        String storageKey = folder + "/" + UUID.randomUUID().toString() + extension;

        Path targetPath = Paths.get(basePath, storageKey);

        // Crear directorios si no existen
        Files.createDirectories(targetPath.getParent());

        Files.copy(inputStream, targetPath);

        log.info("Archivo almacenado desde InputStream: {} -> {}", fileName, storageKey);
        return storageKey;
    }

     
    public InputStream retrieve(String storageKey) throws Exception {
        Path filePath = Paths.get(basePath, storageKey);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Archivo no encontrado: " + storageKey);
        }

        return Files.newInputStream(filePath);
    }

     
    public String getPublicUrl(String storageKey) {
        return urlPrefix + "/" + storageKey;
    }

     
    public void delete(String storageKey) throws Exception {
        Path filePath = Paths.get(basePath, storageKey);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Archivo eliminado: {}", storageKey);
        }
    }

     
    public boolean exists(String storageKey) {
        Path filePath = Paths.get(basePath, storageKey);
        return Files.exists(filePath);
    }

     
    public long getFileSize(String storageKey) throws Exception {
        Path filePath = Paths.get(basePath, storageKey);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Archivo no encontrado: " + storageKey);
        }

        return Files.size(filePath);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
