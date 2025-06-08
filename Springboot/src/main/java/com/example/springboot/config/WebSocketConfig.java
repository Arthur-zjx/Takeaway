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
        // SockJS fallback endpoint
        registry.addEndpoint("/ws/orders")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();

        // Native WebSocket endpoint
        registry.addEndpoint("/ws/orders")
                .setAllowedOriginPatterns("http://localhost:5173");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for client subscriptions
        registry.enableSimpleBroker("/topic");
        // Prefix for messages sent from client (if needed)
        registry.setApplicationDestinationPrefixes("/app");
    }
}
