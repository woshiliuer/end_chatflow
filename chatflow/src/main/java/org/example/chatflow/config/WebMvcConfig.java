package org.example.chatflow.config;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.handler.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author by zzr
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final SecurityProperties securityProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");
        if (securityProperties.getWhitelist() != null && !securityProperties.getWhitelist().isEmpty()) {
            registration.excludePathPatterns(securityProperties.getWhitelist().toArray(String[]::new));
        }
    }
}
