package com.tablelog.tablelogback.domain.chat.repository;

import com.tablelog.tablelogback.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    /**
     * 특정 채팅방의 메시지 목록 조회 (최신순)
     */
    @Query("SELECT c FROM Chat c WHERE c.roomId = :roomId ORDER BY c.createdAt DESC")
    List<Chat> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") String roomId);
    
    /**
     * 특정 채팅방의 메시지 목록 조회 (오래된순)
     */
    @Query("SELECT c FROM Chat c WHERE c.roomId = :roomId ORDER BY c.createdAt ASC")
    List<Chat> findByRoomIdOrderByCreatedAtAsc(@Param("roomId") String roomId);
    
    /**
     * 특정 채팅방의 메시지 개수 조회
     */
    long countByRoomId(String roomId);
    
    /**
     * 특정 사용자의 채팅 메시지 조회
     */
    List<Chat> findBySenderOrderByCreatedAtDesc(String sender);

    /**
     * 전체 채팅 메시지 조회 (최신순)
     */
    List<Chat> findAllByOrderByCreatedAtDesc();

    interface ChatRoomSummary {
        String getRoomId();
        LocalDateTime getLastCreatedAt();
        Long getMessageCount();
    }

    @Query("SELECT c.roomId as roomId, MAX(c.createdAt) as lastCreatedAt, COUNT(c.id) as messageCount " +
           "FROM Chat c WHERE c.roomId LIKE CONCAT(:email, '--%') OR c.roomId LIKE CONCAT('%--', :email) " +
           "GROUP BY c.roomId ORDER BY lastCreatedAt DESC")
    List<ChatRoomSummary> findOwnedRoomsForParticipant(@Param("email") String email);

    long countByRoomIdAndReceiverEmailAndReadAtIsNull(String roomId, String receiverEmail);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Chat c SET c.readAt = CURRENT_TIMESTAMP WHERE c.roomId = :roomId AND c.receiverEmail = :receiverEmail AND c.readAt IS NULL")
    int markRoomRead(@Param("roomId") String roomId, @Param("receiverEmail") String receiverEmail);

    interface LastMessageProjection {
        String getRoomId();
        String getLastMessage();
        java.time.LocalDateTime getLastCreatedAt();
    }

    @Query("SELECT c.roomId as roomId, c.message as lastMessage, c.createdAt as lastCreatedAt " +
           "FROM Chat c " +
           "WHERE (c.roomId LIKE CONCAT(:userId, '--%') OR c.roomId LIKE CONCAT('%--', :userId)) " +
           "AND c.createdAt = (SELECT MAX(c2.createdAt) FROM Chat c2 WHERE c2.roomId = c.roomId) " +
           "ORDER BY c.createdAt DESC")
    List<LastMessageProjection> findLastMessagesForParticipant(@Param("userId") Long userId);
}
