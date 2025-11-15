package com.tablelog.tablelogback.global.handler;

import com.tablelog.tablelogback.domain.chat.service.ChatService;
import com.tablelog.tablelogback.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StompHandler.class);
    private final ChatService chatService;

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
                LOGGER.info("📌 STOMP SUBSCRIBE 요청: 세션 {} -> 구독 {}", accessor.getSessionId(), destination);
                
                // 채팅방 구독인 경우 roomId 정규화
                if (destination.startsWith("/sub/chat/room/")) {
                    String rawRoomId = destination.substring("/sub/chat/room/".length());
                    LOGGER.info("🔍 원본 roomId: {}", rawRoomId);
                    
                    // 사용자 정보 가져오기
                    User currentUser = chatService.getCurrentUser(accessor);
                    if (currentUser != null) {
                        // roomId 정규화: 10--6과 6--10을 같은 채팅방으로 처리
                        String normalizedRoomId = normalizeRoomId(rawRoomId, currentUser.getId());
                        LOGGER.info("🔄 roomId 정규화: {} -> {}", rawRoomId, normalizedRoomId);
                        
                        // 정규화된 roomId로 destination 변경
                        String normalizedDestination = "/sub/chat/room/" + normalizedRoomId;
                        accessor.setDestination(normalizedDestination);
                        
                        // 권한 검증
                        try {
                            chatService.validateChatRoomSubscription(normalizedRoomId, accessor);
                            LOGGER.info("✅ 구독 허용: 세션 {} -> 정규화된 채팅방 {}", 
                                accessor.getSessionId(), normalizedDestination);
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("🚫 구독 거부: {}", e.getMessage());
                            throw e;
                        }
                    } else {
                        LOGGER.warn("⚠️ 인증되지 않은 사용자의 구독 시도: {}", destination);
                        throw new IllegalArgumentException("인증이 필요합니다.");
                    }
                } else {
                    LOGGER.info("📌 일반 구독: {}", destination);
                }
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
        
        // destination이 변경된 경우 새로운 메시지 생성
        if (accessor.isMutable()) {
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }
        return message;
    }

    /**
     * roomId를 정규화하여 항상 동일한 형식으로 반환
     * 예: "10--6" 또는 "6--10" -> "6--10"
     * @param rawRoomId 원본 roomId
     * @param currentUserId 현재 사용자 ID
     * @return 정규화된 roomId
     */
    private String normalizeRoomId(String rawRoomId, Long currentUserId) {
        if (rawRoomId == null || rawRoomId.isEmpty()) {
            throw new IllegalArgumentException("roomId는 null이거나 비어있을 수 없습니다.");
        }

        String[] parts = rawRoomId.split("--", 2);
        if (parts.length != 2) {
            LOGGER.warn("⚠️ roomId 형식 오류: {}", rawRoomId);
            return rawRoomId; // 형식이 맞지 않으면 원본 반환
        }

        try {
            Long part0 = Long.parseLong(parts[0].trim());
            Long part1 = Long.parseLong(parts[1].trim());
            // buildPairRoomId를 사용하여 정규화
            return chatService.buildPairRoomId(part0, part1);
        } catch (NumberFormatException e) {
            LOGGER.warn("⚠️ roomId 파싱 실패: {}", rawRoomId);
            return rawRoomId; // 파싱 실패 시 원본 반환
        }
    }
}
