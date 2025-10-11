package com.tablelog.tablelogback.domain.chat.repository;

import com.tablelog.tablelogback.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    List<Chat> findByUsernameOrderByCreatedAtDesc(String username);
}
