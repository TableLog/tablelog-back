package com.tablelog.tablelogback.domain.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.event.EventListener;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        LOGGER.info("User connected to chat");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        LOGGER.info("User disconnected: {}", sessionId);
    }

    @MessageMapping("/chat/send")
    public void sendMessage(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        messagingTemplate.convertAndSend("/sub/chat/" + roomId, message);
    }
}
