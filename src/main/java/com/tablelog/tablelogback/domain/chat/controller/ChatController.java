package com.tablelog.tablelogback.domain.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 클라이언트 WebSocket 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        LOGGER.info("✅ User connected to chat");
    }

    /**
     * 클라이언트가 특정 채널을 구독할 때 실행
     */
    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination(); // 구독한 채널 정보
        LOGGER.info("📌 User subscribed to: {}", destination);
    }

    /**
     * 클라이언트 WebSocket 연결 해제 이벤트
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        LOGGER.info("❌ User disconnected: {}", sessionId);
    }

    /**
     * 클라이언트가 메시지를 보낼 때 실행되는 메서드
     * @param message 메시지 (JSON 형식)
     */
    @MessageMapping("/chat/send")
    public void sendMessage(Map<String, Object> message) {
        try {
            if (!message.containsKey("roomId")) {
                LOGGER.warn("🚨 Message rejected: roomId is missing");
                return ;
            }

            String roomId = message.get("roomId").toString();
            LOGGER.info("📩 Received message in room {}: {}", roomId, message);

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, message);
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: ", e);
        }
    }
}
