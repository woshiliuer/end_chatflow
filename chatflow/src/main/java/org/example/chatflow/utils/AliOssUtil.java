package org.example.chatflow.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.config.AliOssProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Utility that wraps common Aliyun OSS operations.
 *
 * @author by zzr
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AliOssUtil {

    private final OSS ossClient;
    private final AliOssProperties aliOssProperties;

    /**
     * Upload a file to OSS.
     *
     * @param bytes      file data
     * @param objectName object key to persist to
     * @return public access URL
     */
    public String upload(byte[] bytes, String objectName) {
        Objects.requireNonNull(bytes, "bytes must not be null");
        Objects.requireNonNull(objectName, "objectName must not be null");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            ossClient.putObject(aliOssProperties.getBucketName(), objectName, inputStream);
        } catch (OSSException oe) {
            log.error("OSS rejected the upload, requestId={}, errorCode={}, message={}",
                    oe.getRequestId(), oe.getErrorCode(), oe.getErrorMessage(), oe);
            throw oe;
        } catch (ClientException ce) {
            log.error("Client error while communicating with OSS: {}", ce.getMessage(), ce);
            throw ce;
        }

        String url = buildObjectUrl(objectName);
        log.info("File uploaded successfully: {}", url);
        return url;
    }

    private String buildObjectUrl(String objectName) {
        String endpoint = aliOssProperties.getEndpoint();
        String bucket = aliOssProperties.getBucketName();

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException("AliOssProperties.endpoint must not be blank");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("AliOssProperties.bucketName must not be blank");
        }

        NormalizedEndpoint normalizedEndpoint = normalizeEndpoint(endpoint);
        return String.format(
                "%s://%s.%s/%s",
                normalizedEndpoint.scheme(),
                bucket,
                normalizedEndpoint.authority(),
                objectName
        );
    }

    private NormalizedEndpoint normalizeEndpoint(String endpoint) {
        try {
            URI uri = new URI(endpoint);
            if (uri.getScheme() != null && uri.getHost() != null) {
                int port = uri.getPort();
                String authority = port > 0 ? uri.getHost() + ":" + port : uri.getHost();
                return new NormalizedEndpoint(uri.getScheme(), authority);
            }
        } catch (URISyntaxException ignored) {
            // fall through to manual normalization
        }

        String sanitized = endpoint.replaceFirst("^https?://", "");
        return new NormalizedEndpoint("https", sanitized);
    }

    private record NormalizedEndpoint(String scheme, String authority) {
    }
}
