package com.tablelog.tablelogback.domain.chat.controller;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.chat.service.ChatService;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatRoomLastMessageResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Chat", description = "채팅 REST API")
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
     * (이미 StompHandler에서 검증이 완료된 후 실행됨)
     */
    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination(); // 구독한 채널 정보
        String sessionId = accessor.getSessionId();
        
        if (destination != null && destination.startsWith("/sub/chat/room/")) {
            String roomId = destination.substring("/sub/chat/room/".length());
            LOGGER.info("✅ 구독 완료 - 세션: {}, 채팅방: {}", sessionId, roomId);
            
            // Service에서 사용자 정보 확인 (선택적 - 로깅용)
            User currentUser = chatService.getCurrentUser(accessor);
            if (currentUser != null) {
                LOGGER.info("👤 구독자 정보: {} (Email: {})", currentUser.getNickname(), currentUser.getEmail());
            }
        } else {
            LOGGER.info("📌 User subscribed - 세션: {}, destination: {}", sessionId, destination);
        }
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
                return;
            }

            String rawRoomId = message.get("roomId").toString();
            LOGGER.info("📩 Received message in room {}: {}", rawRoomId, message);

            // 사용자 정보 가져오기 (인증 필수)
            User currentUser = chatService.getCurrentUser(accessor);
            if (currentUser == null) {
                LOGGER.warn("⚠️ 인증되지 않은 사용자의 채팅 메시지 전송 시도: roomId={}", rawRoomId);
                return; // 인증되지 않은 사용자의 메시지는 거부
            }

            LOGGER.info("👤 Current user: {} (ID: {}, Email: {})", 
                currentUser.getNickname(), currentUser.getId(), currentUser.getEmail());

            // roomId 정규화: 10--6과 6--10을 같은 채팅방으로 처리
            String roomId = normalizeRoomId(rawRoomId, currentUser.getId());
            LOGGER.info("🔄 roomId 정규화: {} -> {}", rawRoomId, roomId);

            // 권한 검증: roomId가 사용자의 userId를 포함하는지 확인 (2인 룸 규칙)
            Long userId = currentUser.getId();
            if (!chatService.isParticipant(roomId, userId)) {
                LOGGER.warn("🚫 권한 없음: 사용자 ID {}가 채팅방 {}에 메시지 전송 시도", userId, roomId);
                return; // 권한 없는 채팅방에 메시지 전송 거부
            }

            LOGGER.info("✅ 권한 확인 완료: 사용자 ID {}가 채팅방 {}에 메시지 전송", userId, roomId);

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
                    message.get("messageType") != null ? message.get("messageType").toString() : "TEXT",
                    currentUser.getEmail()
                );
                
                chatService.saveChatMessage(chatMessageServiceRequestDto);
                LOGGER.info("💾 채팅 메시지가 데이터베이스에 저장되었습니다: {}", message.get("message"));
            } catch (Exception e) {
                LOGGER.error("❌ 채팅 메시지 저장 실패: ", e);
            }

            // 메시지를 구독자들에게 전송 (정규화된 roomId 사용)
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, message);
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: ", e);
        }
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

    // 전체 채팅 메시지 조회 (최신순)
    @Operation(summary = "전체 채팅 메시지 조회", description = "전체 채팅 메시지를 최신순으로 조회합니다.")
    @GetMapping("/chats")
    public ResponseEntity<List<ChatMessageServiceResponseDto>> getAllChats() {
        List<ChatMessageServiceResponseDto> chats = chatService.getAllChatMessages();
        return ResponseEntity.ok(chats);
    }

    // 특정 채팅방 메시지 조회 (order=desc|asc, default=desc)
    @Operation(summary = "특정 채팅방 메시지 조회", description = "채팅방 메시지를 정렬 옵션과 함께 조회합니다. order=asc|desc (기본 asc)")
    @GetMapping("/chats/rooms/{roomId}")
    public ResponseEntity<List<ChatMessageServiceResponseDto>> getRoomChats(
        @Parameter(description = "채팅방 ID(userIdA--userIdB)") @PathVariable("roomId") String roomId,
        @Parameter(description = "정렬(order=asc|desc), 기본 asc") @RequestParam(name = "order", defaultValue = "asc") String order,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        LOGGER.info("📋 채팅방 메시지 조회 요청: roomId={}, order={}, user={}", 
            roomId, order, userDetails != null ? userDetails.user().getEmail() : "null");
        
        // roomId 정규화: 10--6과 6--10을 같은 채팅방으로 처리
        String normalizedRoomId = normalizeRoomId(roomId, userDetails.user().getId());
        LOGGER.info("🔄 roomId 정규화: {} -> {}", roomId, normalizedRoomId);
        
        List<ChatMessageServiceResponseDto> chats =
            "asc".equalsIgnoreCase(order)
                ? chatService.getChatMessagesAscWithAuth(normalizedRoomId, userDetails.user())
                : chatService.getChatMessagesWithAuth(normalizedRoomId, userDetails.user());
        
        LOGGER.info("✅ 채팅방 {} 메시지 조회 완료: {}개", normalizedRoomId, chats.size());
        return ResponseEntity.ok(chats);
    }

    /**
     * WebSocket을 통한 채팅방 목록 조회
     * 
     * 클라이언트 사용 방법:
     * 1. WebSocket 연결 후 사용자별 채팅방 목록 구독
     *    stompClient.subscribe('/sub/chat/rooms/{userId}', function(message) {
     *        const rooms = JSON.parse(message.body);
     *        console.log('채팅방 목록:', rooms);
     *    });
     * 
     * 2. /pub/chat/rooms로 요청 전송 (빈 객체 또는 null)
     *    stompClient.send('/pub/chat/rooms', {}, JSON.stringify({}));
     * 
     * 3. 응답은 /sub/chat/rooms/{userId}로 수신됨
     * 
     * @param request 요청 메시지 (빈 Map 또는 null 가능)
     * @param accessor STOMP 헤더 접근자
     */
    @MessageMapping("/chat/rooms")
    public void getOwnedRoomsViaWebSocket(
            @Payload(required = false) Map<String, Object> request,
            StompHeaderAccessor accessor
    ) {
        try {
            // 사용자 정보 가져오기 (인증 필수)
            User currentUser = chatService.getCurrentUser(accessor);
            if (currentUser == null) {
                LOGGER.warn("⚠️ 인증되지 않은 사용자의 채팅방 목록 조회 시도");
                return;
            }

            LOGGER.info("📋 WebSocket 채팅방 목록 조회 요청: user={} (ID: {})",
                currentUser.getEmail(), currentUser.getId());

            List<ChatRoomLastMessageResponseDto> rooms = chatService.getOwnedChatRooms(currentUser);
            
            LOGGER.info("✅ 채팅방 목록 조회 완료: {}개 방", rooms.size());
            
            // 사용자별 구독 채널로 응답 전송 (/sub/chat/rooms/{userId})
            String destination = "/sub/chat/rooms/" + currentUser.getId();
            messagingTemplate.convertAndSend(destination, rooms);
            
            LOGGER.info("📤 채팅방 목록 전송 완료: destination={}", destination);
        } catch (Exception e) {
            LOGGER.error("❌ 채팅방 목록 조회 실패: ", e);
        }
    }

    // 로그인한 사용자가 소유한 채팅방 목록 조회 (REST API - 하위 호환성 유지)
    @Operation(summary = "내 채팅방 목록(마지막 메시지) 조회", description = "내가 참가자인 채팅방을 최근 대화 순으로 조회하고, 각 방의 마지막 메시지와 미읽음 카운트를 반환합니다.")
    @GetMapping("/chats/rooms")
    public ResponseEntity<List<ChatRoomLastMessageResponseDto>> getOwnedRooms(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        LOGGER.info("📋 소유 채팅방 목록 조회 요청 (REST): user={}",
            userDetails != null ? userDetails.user().getEmail() : "null");
        List<ChatRoomLastMessageResponseDto> rooms = chatService.getOwnedChatRooms(userDetails.user());
        return ResponseEntity.ok(rooms);
    }

    // 방 미열람 개수
    @Operation(summary = "방 미읽음 개수 조회", description = "현재 로그인 사용자가 수신자 기준으로 해당 방의 미읽음 메시지 개수를 반환합니다.")
    @GetMapping("/chats/rooms/{roomId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(
        @Parameter(description = "채팅방 ID(userIdA--userIdB)") @PathVariable("roomId") String roomId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // roomId 정규화: 10--6과 6--10을 같은 채팅방으로 처리
        String normalizedRoomId = normalizeRoomId(roomId, userDetails.user().getId());
        LOGGER.info("🔄 roomId 정규화: {} -> {}", roomId, normalizedRoomId);
        
        long count = chatService.getUnreadCount(normalizedRoomId, userDetails.user());
        return ResponseEntity.ok(count);
    }

}
