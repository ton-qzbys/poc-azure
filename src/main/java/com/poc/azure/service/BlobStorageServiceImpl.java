package com.poc.azure.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "mock.azure.blobstorage.service.enabled", havingValue = "false", matchIfMissing = true)
public class BlobStorageServiceImpl implements BlobStorageService {

    private final Logger LOGGER = LoggerFactory.getLogger(BlobStorageServiceImpl.class);
    private final CloudBlobContainer cloudBlobContainer;

    @Override
    public URI uploadPicture(MultipartFile multipartFile) {
        URI uri;
        String multipartName = multipartFile.getName().replaceAll("[\n|\r|\t]", "_");
        LOGGER.info("Profile image uploading, image name: {}", multipartName);

        try {
            String fileName = String.join(".", UUID.randomUUID().toString(), multipartFile.getOriginalFilename());
            CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(fileName);
            blob.upload(multipartFile.getInputStream(), -1);
            uri = blob.getUri();
        } catch (URISyntaxException | StorageException | IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(uri).orElseThrow(RuntimeException::new);
    }
}
