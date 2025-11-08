package com.tablelog.tablelogback.domain.chat.service;

import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatRoomLastMessageResponseDto;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.List;

public interface ChatService {

    /**
     * 채팅방 ID 생성
     * @param email 사용자 이메일
     * @return 생성된 채팅방 ID
     */
    String createChatRoomId(String email);

    /**
     * 채팅 메시지 저장
     * @param chatMessageServiceRequestDto 저장할 채팅 메시지 DTO
     * @return 저장된 채팅 메시지 DTO
     */
    ChatMessageServiceResponseDto saveChatMessage(ChatMessageServiceRequestDto chatMessageServiceRequestDto);

    /**
     * 특정 채팅방의 메시지 목록 조회 (최신순) - 내부용
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessages(String roomId);

    /**
     * 특정 채팅방의 메시지 목록 조회 (오래된순) - 내부용
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesAsc(String roomId);

    /**
     * 특정 채팅방의 메시지 목록 조회 (최신순) - 권한 검증 포함
     * @param roomId 채팅방 ID
     * @param currentUser 현재 로그인한 사용자
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesWithAuth(String roomId, User currentUser);

    /**
     * 특정 채팅방의 메시지 목록 조회 (오래된순) - 권한 검증 포함
     * @param roomId 채팅방 ID
     * @param currentUser 현재 로그인한 사용자
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesAscWithAuth(String roomId, User currentUser);

    /**
     * 특정 채팅방의 메시지 개수 조회
     * @param roomId 채팅방 ID
     * @return 메시지 개수
     */
    long getChatMessageCount(String roomId);

    /**
     * 특정 사용자의 채팅 메시지 조회
     * @param sender 발신자명
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesByUser(String sender);

    /**
     * 모든 채팅 메시지 전체 조회 (최신순)
     */
    List<ChatMessageServiceResponseDto> getAllChatMessages();

    /**
     * STOMP 헤더에서 현재 인증된 사용자 정보 가져오기
     * @param accessor STOMP 헤더 접근자
     * @return User 객체 또는 null
     */
    User getCurrentUser(StompHeaderAccessor accessor);

    /**
     * SecurityContext에서 현재 인증된 사용자 정보 가져오기 (HTTP 요청용)
     * @return User 객체 또는 null
     */
    User getCurrentAuthenticatedUser();

    /**
     * 채팅방 구독 권한 검증
     * @param roomId 채팅방 ID
     * @param accessor STOMP 헤더 접근자
     * @throws IllegalArgumentException 인증 실패 또는 권한 없음 시
     */
    void validateChatRoomSubscription(String roomId, StompHeaderAccessor accessor);

    /**
     * 로그인한 사용자가 소유한 채팅방 목록 조회 (roomId prefix = email-)
     * 최근 대화 순 정렬
     */
    List<ChatRoomLastMessageResponseDto> getOwnedChatRooms(User currentUser);

    /**
     * 두 사용자 ID로 2인 고정 룸ID 생성 (사전순 정렬하여 안정적 생성)
     */
    String buildPairRoomId(Long userIdA, Long userIdB);

    /**
     * 주어진 룸ID가 해당 사용자 ID가 포함된 방인지 확인
     */
    boolean isParticipant(String roomId, Long userId);

    /**
     * 방 미열람 개수 조회(현재 사용자 기준)
     */
    long getUnreadCount(String roomId, User currentUser);

    /**
     * 방 읽음 처리(현재 사용자 기준)
     */
    int markRoomRead(String roomId, User currentUser);
}