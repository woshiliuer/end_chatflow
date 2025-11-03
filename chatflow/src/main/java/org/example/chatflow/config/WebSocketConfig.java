package org.example.chatflow.config;

import lombok.RequiredArgsConstructor;
import org.example.chatflow.handler.WebSocketChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket配置类
 * 使用STOMP协议进行认证，不需要在握手阶段进行认证
 * @author by zzr
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketChannelInterceptor channelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理，用于向客户端广播消息
        registry.enableSimpleBroker("/topic", "/queue");
        // 设置应用程序目标前缀，客户端发送消息时使用
        registry.setApplicationDestinationPrefixes("/app");
        // 设置用户目标前缀，用于点对点消息
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，允许所有来源
        // 认证在STOMP CONNECT阶段通过ChannelInterceptor进行
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 配置客户端入站通道拦截器，用于STOMP消息层面的认证
        registration.interceptors(channelInterceptor);
    }

}
