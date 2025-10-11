package com.tablelog.tablelogback.domain.chat.service;

import com.tablelog.tablelogback.domain.chat.entity.Chat;

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
     * @param chat 저장할 채팅 메시지
     * @return 저장된 채팅 메시지
     */
    Chat saveChatMessage(Chat chat);

    /**
     * 특정 채팅방의 메시지 목록 조회 (최신순)
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록
     */
    List<Chat> getChatMessages(String roomId);

    /**
     * 특정 채팅방의 메시지 목록 조회 (오래된순)
     * @param roomId 채팅방 ID
     * @return 채팅 메시지 목록
     */
    List<Chat> getChatMessagesAsc(String roomId);

    /**
     * 특정 채팅방의 메시지 개수 조회
     * @param roomId 채팅방 ID
     * @return 메시지 개수
     */
    long getChatMessageCount(String roomId);

    /**
     * 특정 사용자의 채팅 메시지 조회
     * @param username 사용자명
     * @return 채팅 메시지 목록
     */
    List<Chat> getChatMessagesByUser(String username);
}