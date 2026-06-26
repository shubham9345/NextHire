package com.AIService.AIService.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * STOMP over WebSocket.
 *
 * React client connects to:
 *   ws://host/ws/interview
 *
 * Then subscribes to:
 *   /user/queue/interview      ← personal events (questions, evals, complete)
 *   /topic/interview/{id}      ← session broadcast (errors)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/interview")
                .setAllowedOriginPatterns("*")   // restrict in prod to your frontend domain
                .withSockJS();                   // fallback for browsers without native WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Simple in-memory broker for /queue (user-specific) and /topic (broadcast)
        registry.enableSimpleBroker("/queue", "/topic");

        // Prefix for messages sent by the client to server (if any)
        registry.setApplicationDestinationPrefixes("/app");

        // Needed for convertAndSendToUser() to work correctly
        registry.setUserDestinationPrefix("/user");
    }
}