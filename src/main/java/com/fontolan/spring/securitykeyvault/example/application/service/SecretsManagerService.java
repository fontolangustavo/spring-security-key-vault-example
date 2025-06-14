package com.fontolan.spring.securitykeyvault.example.application.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.net.URI;

@Service
public class SecretsManagerService {
    private final SecretsManagerClient secretsManagerClient;

    public SecretsManagerService() {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .endpointOverride(URI.create("http://localstack:4566"))
                .region(Region.US_EAST_1)
                .build();
    }

    public String getSecretValue(String secretId) {
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();

        return secretsManagerClient.getSecretValue(getSecretValueRequest).secretString();
    }
}

