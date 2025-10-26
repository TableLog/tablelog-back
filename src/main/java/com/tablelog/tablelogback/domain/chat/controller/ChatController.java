package com.tablelog.tablelogback.domain.chat.controller;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.chat.service.ChatService;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
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
    private final ChatService chatService;

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
    public void handleSubscriptionEvent(SessionSubscribeEvent event
        ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination(); // 구독한 채널 정보
//        LOGGER.info("userDetails :" , userDetails.user());
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
     * @param accessor STOMP 헤더 접근자
     */
    @MessageMapping("/chat/send")
    public void sendMessage(Map<String, Object> message, StompHeaderAccessor accessor) {
        try {
            if (!message.containsKey("roomId")) {
                LOGGER.warn("🚨 Message rejected: roomId is missing");
                return ;
            }

            String roomId = message.get("roomId").toString();
            LOGGER.info("📩 Received message in room {}: {}", roomId, message);

            // 디버깅: 모든 헤더 정보 출력
            LOGGER.info("🔍 All STOMP headers:");
            accessor.toMap().forEach((key, value) -> 
                LOGGER.info("  {}: {}", key, value)
            );
            
            // 디버깅: 세션 속성 출력
            LOGGER.info("🔍 Session attributes:");
            accessor.getSessionAttributes().forEach((key, value) -> 
                LOGGER.info("  {}: {}", key, value)
            );

            // 사용자 정보 가져오기
            User currentUser = chatService.getCurrentUser(accessor);
            if (currentUser != null) {
                LOGGER.info("👤 Current user: {} (ID: {}, Email: {})", 
                    currentUser.getNickname(), currentUser.getId(), currentUser.getEmail());
                
                // 사용자 정보를 메시지에 추가
                message.put("userId", currentUser.getId());
                message.put("userNickname", currentUser.getNickname());
                message.put("userEmail", currentUser.getEmail());
                
                // 데이터베이스에 채팅 메시지 저장
                try {
                    ChatMessageServiceRequestDto chatMessageServiceRequestDto = new ChatMessageServiceRequestDto(
                        roomId,
                        currentUser.getNickname(),
                        message.get("message").toString(),
                        message.get("messageType") != null ? message.get("messageType").toString() : "TEXT"
                    );
                    
                    chatService.saveChatMessage(chatMessageServiceRequestDto);
                    LOGGER.info("💾 채팅 메시지가 데이터베이스에 저장되었습니다: {}", message.get("message"));
                } catch (Exception e) {
                    LOGGER.error("❌ 채팅 메시지 저장 실패: ", e);
                }
            } else {
                LOGGER.warn("⚠️ No authenticated user found");
                message.put("userId", "anonymous");
                message.put("userNickname", "anonymous");
                
                // 익명 사용자의 경우에도 저장 (선택사항)
                try {
                    ChatMessageServiceRequestDto chatMessageServiceRequestDto = new ChatMessageServiceRequestDto(
                        roomId,
                        "anonymous",
                        message.get("message").toString(),
                        message.get("messageType") != null ? message.get("messageType").toString() : "TEXT"
                    );
                    
                    chatService.saveChatMessage(chatMessageServiceRequestDto);
                    LOGGER.info("💾 익명 사용자 채팅 메시지가 데이터베이스에 저장되었습니다: {}", message.get("message"));
                } catch (Exception e) {
                    LOGGER.error("❌ 익명 사용자 채팅 메시지 저장 실패: ", e);
                }
            }

            // 메시지를 구독자들에게 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, message);
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: ", e);
        }
    }
}
