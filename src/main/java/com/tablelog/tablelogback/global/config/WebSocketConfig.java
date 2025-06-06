package com.tablelog.tablelogback.global.config;

import com.tablelog.tablelogback.domain.board.controller.CustomHandshakeInterceptor;
import com.tablelog.tablelogback.global.handler.StompHandler;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.ChannelRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;  // StompHandler 주입
    private final JwtUtil jwtUtil;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new CustomHandshakeInterceptor(jwtUtil) {
                    @Override
                    public void afterHandshake(ServerHttpRequest request,
                        ServerHttpResponse response, WebSocketHandler wsHandler,
                        Exception exception) {

                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SockJS 지원
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");  // 구독 Prefix
        registry.setApplicationDestinationPrefixes("/pub");  // 발행 Prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);  // StompHandler 등록
    }
}
