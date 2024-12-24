//package com.acorn.movielink.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.*;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws") // WebSocket 엔드포인트
//                .setAllowedOrigins("*") // 필요에 따라 변경 (예: 특정 도메인만 허용)
//                .withSockJS(); // SockJS 지원
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic"); // 메시지 브로커 경로
//        registry.setApplicationDestinationPrefixes("/app"); // 애플리케이션 목적지 접두사
//    }
//}
//실시간 동접자