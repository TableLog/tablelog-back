package com.tablelog.tablelogback.domain.chat.service;

import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
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
     * 특정 채팅방의 메시지 목록 조회 (최신순)
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessages(String roomId);

    /**
     * 특정 채팅방의 메시지 목록 조회 (오래된순)
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesAsc(String roomId);

    /**
     * 특정 채팅방의 메시지 개수 조회
     * @param roomId 채팅방 ID
     * @return 메시지 개수
     */
    long getChatMessageCount(String roomId);

    /**
     * 특정 사용자의 채팅 메시지 조회
     * @param username 사용자명
     * @return 채팅 메시지 목록 DTO
     */
    List<ChatMessageServiceResponseDto> getChatMessagesByUser(String username);

    /**
     * STOMP 헤더에서 현재 인증된 사용자 정보 가져오기
     * @param accessor STOMP 헤더 접근자
     * @return User 객체 또는 null
     */
    User getCurrentUser(StompHeaderAccessor accessor);
}