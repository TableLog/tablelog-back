package com.tablelog.tablelogback.global.handler;

import com.tablelog.tablelogback.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
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
    private final JwtUtil jwtUtil;

    public StompHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == StompCommand.CONNECT) {
            String accessToken = (String) accessor.getSessionAttributes().get("accessToken");
            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
                String email = claims.getSubject();
                LOGGER.info("✅ User connected to chat: {}", email);
                // chatService.createChatRoomId(email); // 필요시 비즈니스 로직 호출
            } else {
                LOGGER.warn("STOMP CONNECT: accessToken missing or invalid (세션 {})",
                    accessor.getSessionId());
                // 필요하다면 연결 거부 처리도 가능
            }
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
        } else if (command == StompCommand.DISCONNECT) {
            LOGGER.info("❌ STOMP DISCONNECTED: 세션 {}", accessor.getSessionId());
        } else if (command != null) {
            LOGGER.debug("📌 처리되지 않은 STOMP 명령어: {} | 세션 {}", command, accessor.getSessionId());
        }
        return message;
    }
}
