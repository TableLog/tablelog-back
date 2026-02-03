package com.tablelog.tablelogback.global.config;

import com.tablelog.tablelogback.global.handler.StompHandler;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.context.annotation.Bean;

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
        // Heartbeat 설정 추가: 10초마다 heartbeat 전송, 20초 타임아웃
        registry.enableSimpleBroker("/sub")
                .setHeartbeatValue(new long[]{10000, 20000})  // [서버→클라이언트, 클라이언트→서버]
                .setTaskScheduler(stompHeartbeatTaskScheduler());
        registry.setApplicationDestinationPrefixes("/pub");  // 발행 Prefix
    }

    /**
     * STOMP SimpleBroker Heartbeat 전송을 위한 스케줄러
     *
     * 주의: Spring 내부에서 사용하는 messageBrokerTaskScheduler 빈 이름과 충돌하지 않도록
     * 별도 메서드/빈 이름을 사용합니다.
     */
    @Bean
    public TaskScheduler stompHeartbeatTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);  // StompHandler 등록
    }
}
