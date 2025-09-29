package com.morago.backend.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final S3Client s3;
    private final String bucket;
    private final String publicBaseUrl;
    private final String keyPrefix;

    public S3StorageService(
            @Value("${app.storage.s3.bucket}") String bucket,
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.public-base-url:}") String publicBaseUrl,
            @Value("${app.storage.s3.key-prefix:}") String keyPrefix
    ) {
        this.bucket = bucket;
        this.keyPrefix = (keyPrefix == null) ? "" : keyPrefix.replaceAll("^/|/$", "");
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        String base = (publicBaseUrl == null || publicBaseUrl.isBlank())
                ? defaultPublicBaseUrl(bucket, region)
                : publicBaseUrl;
        this.publicBaseUrl = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;

        log.info("S3StorageService init: bucket='{}', region='{}', keyPrefix='{}', publicBaseUrl='{}'",
                bucket, region, this.keyPrefix, this.publicBaseUrl);
    }

    private String withPrefix(String key) {
        return (keyPrefix.isEmpty()) ? key : keyPrefix + "/" + key;
    }

    @Override
    public StoredObject store(InputStream in, long contentLength,
                              String originalFilename, String contentType,
                              String key, boolean isPublic) {
        if (contentLength < 0) {
            throw new IllegalArgumentException("contentLength must be provided for S3 upload");
        }

        String s3Key = withPrefix(key);
        PutObjectRequest.Builder req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .metadata(originalFilename == null ? Map.of() : Map.of("original-filename", originalFilename));
        if (contentType != null && !contentType.isBlank()) req = req.contentType(contentType);

        log.info("Uploading to S3: original='{}', key='{}', s3Key='{}', contentType='{}', size={}B",
                originalFilename, key, s3Key, contentType, contentLength);

        try {
            s3.putObject(req.build(), RequestBody.fromInputStream(in, contentLength));
        } catch (S3Exception | SdkClientException e) {
            log.error("S3 upload failed: bucket='{}', s3Key='{}', reason='{}'",
                    bucket, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to upload to S3", e);
        }

        String url = isPublic ? publicBaseUrl + "/" + s3Key : null;
        String ct = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;

        log.info("Uploaded OK: s3://{}/{} -> {}", bucket, s3Key, (url != null ? url : "(private)"));
        return new StoredObject(s3Key, url, contentLength, ct, isPublic);
    }

    @Override
    public void delete(String key) {
        String s3Key = withPrefix(key);
        log.info("Deleting from S3: s3://{}/{}", bucket, s3Key);
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build());
            log.info("Deleted OK: s3://{}/{}", bucket, s3Key);
        } catch (S3Exception | SdkClientException e) {
            log.error("S3 delete failed: bucket='{}', s3Key='{}', reason='{}'",
                    bucket, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to delete from S3", e);
        }
    }

    private static String defaultPublicBaseUrl(String bucket, String region) {
        if ("us-east-1".equals(region)) {
            return "https://" + bucket + ".s3.amazonaws.com";
        }
        return "https://" + bucket + ".s3." + region + ".amazonaws.com";
    }
}