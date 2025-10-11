package com.tablelog.tablelogback.domain.chat.service.Impl;

import com.tablelog.tablelogback.domain.chat.entity.Chat;
import com.tablelog.tablelogback.domain.chat.repository.ChatRepository;
import com.tablelog.tablelogback.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatRepository chatRepository;

    // 세션 접속 시 룸 id 자동으로 생성
    @Override
    public String createChatRoomId(String email) {
        String roomId = email + "-" + UUID.randomUUID().toString();
        LOGGER.info("🏠 채팅방 생성: {} (사용자: {})", roomId, email);
        return roomId;
    }

    // 채팅 메시지 저장
    @Override
    public Chat saveChatMessage(Chat chat) {
        try {
            // 데이터베이스에 채팅 메시지 저장
            Chat savedChat = chatRepository.save(chat);

            LOGGER.info("💾 채팅 메시지 저장: {} (룸: {}, ID: {})",
                savedChat.getMessage(), savedChat.getRoomId(), savedChat.getId());

            return savedChat;

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 저장 실패: ", e);
            throw new RuntimeException("채팅 메시지 저장에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 목록 조회 (최신순)
    @Override
    @Transactional(readOnly = true)
    public List<Chat> getChatMessages(String roomId) {
        try {
            List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
            LOGGER.info("📋 채팅방 {} 메시지 조회: {}개", roomId, chatList.size());
            return chatList;

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 목록 조회 (오래된순)
    @Override
    @Transactional(readOnly = true)
    public List<Chat> getChatMessagesAsc(String roomId) {
        try {
            List<Chat> chatList = chatRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
            LOGGER.info("📋 채팅방 {} 메시지 조회 (오래된순): {}개", roomId, chatList.size());
            return chatList;

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 조회에 실패했습니다.", e);
        }
    }

    // 특정 채팅방의 메시지 개수 조회
    @Override
    @Transactional(readOnly = true)
    public long getChatMessageCount(String roomId) {
        try {
            long count = chatRepository.countByRoomId(roomId);
            LOGGER.info("📊 채팅방 {} 메시지 개수: {}개", roomId, count);
            return count;

        } catch (Exception e) {
            LOGGER.error("❌ 채팅 메시지 개수 조회 실패: ", e);
            throw new RuntimeException("채팅 메시지 개수 조회에 실패했습니다.", e);
        }
    }

    // 특정 사용자의 채팅 메시지 조회
    @Override
    @Transactional(readOnly = true)
    public List<Chat> getChatMessagesByUser(String username) {
        try {
            List<Chat> chatList = chatRepository.findByUsernameOrderByCreatedAtDesc(username);
            LOGGER.info("👤 사용자 {} 메시지 조회: {}개", username, chatList.size());
            return chatList;

        } catch (Exception e) {
            LOGGER.error("❌ 사용자 채팅 메시지 조회 실패: ", e);
            throw new RuntimeException("사용자 채팅 메시지 조회에 실패했습니다.", e);
        }
    }
}