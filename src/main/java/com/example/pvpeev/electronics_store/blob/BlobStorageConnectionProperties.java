package com.example.pvpeev.electronics_store.blob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage.s3")
public class BlobStorageConnectionProperties {
    private URI endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private boolean pathStyleAccess;
}
