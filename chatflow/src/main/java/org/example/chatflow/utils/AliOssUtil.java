package org.example.chatflow.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.config.AliOssProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
     * 上传oss文件
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

    public String upload(MultipartFile file, String objectName) {
        Objects.requireNonNull(file, "file must not be null");
        Objects.requireNonNull(objectName, "objectName must not be null");
        try {
            return upload(file.getBytes(), objectName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * 删除oss文件
     * @param objectName
     */
    public void delete(String objectName) {
        Objects.requireNonNull(objectName, "objectName must not be null");

        try {
            // 调用 OSS SDK 删除对象
            ossClient.deleteObject(aliOssProperties.getBucketName(), objectName);
            log.info("File deleted successfully from OSS: {}", objectName);
        } catch (OSSException oe) {
            log.error("OSS rejected the delete request, requestId={}, errorCode={}, message={}",
                    oe.getRequestId(), oe.getErrorCode(), oe.getErrorMessage(), oe);
            throw oe;
        } catch (ClientException ce) {
            log.error("Client error while communicating with OSS during delete: {}", ce.getMessage(), ce);
            throw ce;
        }
    }

    /**
     * 从 URL 提取对象键
     * @param urlOrKey
     * @return
     */
    public static String toObjectKey(String urlOrKey) {
        // 如果传进来已经是 key（没有 ://），直接规范化返回
        if (urlOrKey == null || !urlOrKey.contains("://")) {
            if (urlOrKey == null) return null;
            String decoded = URLDecoder.decode(urlOrKey, StandardCharsets.UTF_8);
            return decoded.startsWith("/") ? decoded.substring(1) : decoded;
        }
        URI uri = URI.create(urlOrKey);
        String path = uri.getPath(); // e.g. "/avatar/xxx.jpg"
        String decoded = URLDecoder.decode(path, StandardCharsets.UTF_8);
        return decoded.startsWith("/") ? decoded.substring(1) : decoded;
    }

    public static String buildFileName(String dirPrefix, String originalFilename) {
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String prefix = dirPrefix == null ? "" : dirPrefix;
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return prefix + uuid + suffix;
    }

}
