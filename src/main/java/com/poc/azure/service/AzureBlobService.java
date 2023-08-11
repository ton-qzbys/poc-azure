package com.poc.azure.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AzureBlobService {

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;

    @Value("${azure.storage.blob.token}")
    private String sasToken;

    @Value("${azure.storage.blob.url}")
    private String sasUrl;

    public String upload(MultipartFile multipartFile)
            throws IOException {
        BlobClient blob = blobContainerClient.getBlobClient(
                multipartFile.getOriginalFilename()
        );
        blob.upload(
                multipartFile.getInputStream(),
                multipartFile.getSize(),
                true
        );
        return "Upload file {"+multipartFile.getOriginalFilename()+"} success.";
    }

    public String uploadWithFolderName(MultipartFile multipartFile,String folderName)
            throws IOException {
        BlobClient blob = blobContainerClient.getBlobClient(
                folderName+"/"+multipartFile.getOriginalFilename()
        );
        blob.upload(
                multipartFile.getInputStream(),
                multipartFile.getSize(),
                true
        );
        return "Upload file {"+multipartFile.getOriginalFilename()+"} in folder {"+folderName+"} success.";
    }

    public String uploadWithSas(MultipartFile multipartFile)
            throws IOException {
        String containerName = "privatetestcontainer";
        String blobName = multipartFile.getOriginalFilename(); // Replace with your desired blob path
        String url = "https://asdfaergasdfvasdf.blob.core.windows.net/privatetestcontainer/testsas/";

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(sasUrl) // Replace with your account name
                .sasToken(sasToken)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = new BlobClientBuilder()
                .containerName(containerName)
                .blobName(blobName)
                .endpoint(containerClient.getBlobContainerUrl() + "/" + blobName)
                .sasToken(sasToken)
                .buildClient();

        blobClient.upload(
                multipartFile.getInputStream(),
                multipartFile.getSize(),
                true
        );
        return "Upload file {"+multipartFile.getOriginalFilename()+"} success with sasToken.";
    }

    public byte[] getFile(String fileName)
            throws URISyntaxException {
        BlobClient blob = blobContainerClient.getBlobClient(
                fileName
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.download(outputStream);
        final byte[] bytes = outputStream.toByteArray();
        return bytes;

    }

    public List<String> listBlobs() {
        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        List<String> names = new ArrayList<String>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;

    }

    public String deleteBlob(String blobName) {
        BlobClient blob = blobContainerClient.getBlobClient(
                blobName
        );
        blob.delete();
        return "Delete file {"+blobName+"} success.";
    }

    public String createFolder(String folderName){
        blobContainerClient.getBlobClient(folderName + "/") // Note the trailing slash to represent a folder
                .getBlockBlobClient() // This line may change based on your use case
                .upload(new ByteArrayInputStream(new byte[0]), 0); // Uploading an empty blob to represent the folder
        return "Create folder {"+folderName+"} success.";
    }

}
