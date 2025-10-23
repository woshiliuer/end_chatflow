package org.example.chatflow.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by zzr
 */

@Configuration
@Slf4j
@EnableConfigurationProperties(AliOssProperties.class)
public class OssConfiguration {

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(AliOssProperties aliOssProperties) {
        log.info("Initializing Aliyun OSS client, endpoint: {}", aliOssProperties.getEndpoint());
        return new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret()
        );
    }
}
