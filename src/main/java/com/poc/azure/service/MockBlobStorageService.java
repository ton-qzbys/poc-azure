package com.poc.azure.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Service
@ConditionalOnProperty(value = "mock.azure.blobstorage.service.enabled", havingValue = "true")
public class MockBlobStorageService implements BlobStorageService{
    @Override
    public URI uploadPicture(MultipartFile multipartFile) {
        return URI.create("https://mahallemstorage.blob.core.windows.net/testmahallem/mordor_ale.png");
    }
}
