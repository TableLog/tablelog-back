package com.tablelog.tablelogback.global.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StompHandler.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == StompCommand.CONNECT) {
            String accessToken = (String) accessor.getSessionAttributes().get("accessToken");
            LOGGER.info("🔍 CONNECT 시도 - 세션: {}, accessToken 존재: {}", accessor.getSessionId(), accessToken != null);
            LOGGER.info("✅ STOMP CONNECTED: 세션 {}", accessor.getSessionId());
        } else if (command == StompCommand.SUBSCRIBE) {
            String destination = accessor.getDestination();
            if (destination != null) {
                LOGGER.info("📌 STOMP SUBSCRIBED: 세션 {} -> 구독 {}", accessor.getSessionId(),
                    destination);
            } else {
                LOGGER.warn("⚠️ STOMP SUBSCRIBE 요청이 왔지만 destination이 없음! 세션 {}",
                    accessor.getSessionId());
            }
        } else if (command == StompCommand.SEND) {
            // SEND 시 헤더에서 토큰을 찾아 세션에 저장
            LOGGER.info("📤 STOMP SEND 처리 시작 - 세션: {}", accessor.getSessionId());
            
            // 모든 헤더 로그 출력 (Native 헤더)
            LOGGER.info("🔍 모든 Native 헤더: {}", accessor.toNativeHeaderMap());
            
            // STOMP 헤더도 확인
            LOGGER.info("🔍 STOMP 헤더 (toMap): {}", accessor.toMap());
            
            String accessToken = null;
            
            // 1. Native 헤더에서 accessToken 찾기
            accessToken = accessor.getFirstNativeHeader("accessToken");
            LOGGER.info("🔍 Native 헤더 accessToken: {}", accessToken != null ? "있음" : "없음");
            
            // 2. X-Access-Token 헤더
            if (accessToken == null) {
                accessToken = accessor.getFirstNativeHeader("X-Access-Token");
                LOGGER.info("🔍 X-Access-Token 헤더: {}", accessToken != null ? "있음" : "없음");
            }
            
            // 3. Authorization 헤더
            if (accessToken == null) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                LOGGER.info("🔍 Authorization 헤더: {}", authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) : "없음");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    accessToken = authHeader.substring(7);
                }
            }
            
            if (accessToken != null && accessor.getSessionAttributes() != null) {
                accessor.getSessionAttributes().put("accessToken", accessToken);
                LOGGER.info("✅ SEND 시 토큰을 세션에 저장: 세션 {}, 토큰 앞부분: {}", 
                    accessor.getSessionId(), accessToken.substring(0, Math.min(20, accessToken.length())));
            } else {
                LOGGER.warn("⚠️ SEND 시 토큰을 찾지 못함 - 헤더에서 토큰 없음");
            }
        } else if (command == StompCommand.DISCONNECT) {
            LOGGER.info("❌ STOMP DISCONNECTED: 세션 {}", accessor.getSessionId());
        } else if (command != null) {
            LOGGER.debug("📌 처리되지 않은 STOMP 명령어: {} | 세션 {}", command, accessor.getSessionId());
        }
        return message;
    }
}
