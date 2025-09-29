package com.morago.backend.service.storage;

import java.io.InputStream;

public interface StorageService {

    record StoredObject(String key, String publicUrl, long size, String contentType, boolean isPublic) {}

    StoredObject store(InputStream in,
                       long contentLength,
                       String originalFilename,
                       String contentType,
                       String key,
                       boolean isPublic);

    void delete(String key);
}