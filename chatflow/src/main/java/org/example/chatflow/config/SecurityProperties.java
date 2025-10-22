package org.example.chatflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by zzr
 */
@Data
@Component
@ConfigurationProperties(prefix = "chatflow.security")
public class SecurityProperties {

    /**
     * 白名单
     */
    private List<String> whitelist = new ArrayList<>();
}

