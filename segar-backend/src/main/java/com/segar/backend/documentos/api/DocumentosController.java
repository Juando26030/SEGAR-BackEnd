package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import java.net.URL;



@RestController
@RequestMapping("/api/documentos")
public class DocumentosController {

    private final Optional<Storage> storage;

    @Autowired
    public DocumentosController(@Autowired(required = false) Storage storage) {
        this.storage = Optional.ofNullable(storage);
    }

    @PostMapping("/signed-url")
    public ResponseEntity<?> generateSignedUrl(@RequestBody SignedUrlRequest request) {
        if (!storage.isPresent()) {
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Google Cloud Storage is not configured. Please enable GCP in production configuration.");
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(request.getBucketName(), request.getObjectName())
            .setContentType(request.getContentType())
            .build();

        URL signedUrl = storage.get().signUrl(
                blobInfo,
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withContentType()
        );

        return ResponseEntity.ok(signedUrl.toString());
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
