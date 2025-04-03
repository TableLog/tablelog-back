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

        if (command != null) {
            String sessionId = accessor.getSessionId();
            LOGGER.debug("🔍 STOMP Command Received: {} | 세션 {}", command, sessionId);

            switch (command) {
                case CONNECT:
                    LOGGER.info("✅ STOMP CONNECTED: 세션 {}", sessionId);
                    break;
                case SUBSCRIBE:
                    String destination = accessor.getDestination();
                    if (destination != null) {
                        LOGGER.info("📌 STOMP SUBSCRIBED: 세션 {} -> 구독 {}", sessionId, destination);
                    } else {
                        LOGGER.warn("⚠️ STOMP SUBSCRIBE 요청이 왔지만 destination이 없음! 세션 {}", sessionId);
                    }
                    break;
                case DISCONNECT:
                    LOGGER.info("❌ STOMP DISCONNECTED: 세션 {}", sessionId);
                    break;
                default:
                    LOGGER.debug("📌 처리되지 않은 STOMP 명령어: {} | 세션 {}", command, sessionId);
                    break;
            }
        }
        return message;
    }
}
