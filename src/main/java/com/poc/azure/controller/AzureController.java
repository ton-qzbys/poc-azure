package com.poc.azure.controller;


import com.azure.storage.blob.*;
import com.poc.azure.service.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class AzureController {

    @Autowired
    private AzureBlobService azureBlobService;

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam MultipartFile file,
            @RequestParam String folderName
    ) throws IOException {
        String fileName;
        if(folderName.isEmpty()){
            fileName = azureBlobService.upload(file);
        } else {
            fileName = azureBlobService.uploadWithFolderName(file,folderName);
        }
        return ResponseEntity.ok(fileName);
    }


    @PostMapping("/with-sas")
    public ResponseEntity<String> uploadWithSAS(
            @RequestParam MultipartFile file
    ) throws IOException {
        String fileName = azureBlobService.uploadWithSas(file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllBlobs() {

        List<String> items = azureBlobService.listBlobs();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping
    public ResponseEntity<String> delete
            (@RequestParam String fileName) {

        String response = azureBlobService.deleteBlob(fileName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> getFile
            (@RequestParam String fileName)
            throws URISyntaxException {

        ByteArrayResource resource =
                new ByteArrayResource(azureBlobService
                        .getFile(fileName));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + fileName + "\"");

        return ResponseEntity.ok().contentType(MediaType
                        .APPLICATION_OCTET_STREAM)
                .headers(headers).body(resource);
    }
}
