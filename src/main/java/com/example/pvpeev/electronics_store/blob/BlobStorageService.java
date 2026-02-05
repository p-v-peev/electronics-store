package com.example.pvpeev.electronics_store.blob;

import com.example.pvpeev.electronics_store.advice.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlobStorageService {

    private final BlobStorageConnectionProperties properties;

    private final S3Client s3Client;

    public String getFileUrl(String bucketName, UUID key) {
        return String.format("%s/%s/%s", properties.getEndpoint(), bucketName, key);
    }

    public void uploadFile(String bucketName, UUID key, MultipartFile file) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key.toString())
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new FileUploadException();
        }
    }
}
