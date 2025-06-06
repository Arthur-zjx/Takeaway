package com.example.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS 回退端点
        registry.addEndpoint("/ws/orders")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();

        // 原生 WebSocket 端点
        registry.addEndpoint("/ws/orders")
                .setAllowedOriginPatterns("http://localhost:5173");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅前缀
        registry.enableSimpleBroker("/topic");
        // 客户端发送前缀（如果需要）
        registry.setApplicationDestinationPrefixes("/app");
    }
}
