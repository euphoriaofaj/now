package com.morago.backend.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${app.storage.local.root}")
    private String root;

    @Value("${app.storage.public-base-url}")
    private String publicBaseUrl;

    @Override
    public StoredObject store(InputStream in,
                              long contentLength,
                              String originalFilename,
                              String contentType,
                              String key,
                              boolean isPublic) {
        Path target = Path.of(root, key).normalize().toAbsolutePath();

        log.info("Saving file: original='{}', key='{}', contentType='{}', size={} -> {}",
                originalFilename, key, contentType, contentLength, target);

        try {
            Files.createDirectories(target.getParent());
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file locally", e);
        }

        String url = isPublic
                ? (publicBaseUrl.endsWith("/") ? publicBaseUrl + key : publicBaseUrl + "/" + key)
                : null;

        long size = contentLength >= 0 ? contentLength : trySize(target);
        String ct = contentType != null ? contentType : "application/octet-stream";
        return new StoredObject(key, url, size, ct, isPublic);
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(Path.of(root, key));
        } catch (IOException ignored) {}
    }

    private static long trySize(Path p) {
        try { return Files.size(p); } catch (IOException e) { return -1; }
    }
}