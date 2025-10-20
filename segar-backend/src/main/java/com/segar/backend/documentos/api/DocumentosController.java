package com.segar.backend.documentos.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import java.net.URL;



@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentosController {

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
