package com.segar.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Abstracción para proveedores de almacenamiento de archivos
 * Permite usar diferentes implementaciones (Local, S3, MinIO, etc.)
 */
public interface FileStorageService {

    /**
     * Almacena un archivo y devuelve la clave de almacenamiento
     */
    String store(MultipartFile file, String folder) throws Exception;

    /**
     * Almacena un InputStream como archivo
     */
    String store(InputStream inputStream, String fileName, String folder, String contentType) throws Exception;

    /**
     * Recupera un archivo por su clave de almacenamiento
     */
    InputStream retrieve(String storageKey) throws Exception;

    /**
     * Genera una URL pública para un archivo (si aplica)
     */
    String getPublicUrl(String storageKey);

    /**
     * Elimina un archivo por su clave de almacenamiento
     */
    void delete(String storageKey) throws Exception;

    /**
     * Verifica si un archivo existe
     */
    boolean exists(String storageKey);

    /**
     * Obtiene el tamaño de un archivo
     */
    long getFileSize(String storageKey) throws Exception;
}
