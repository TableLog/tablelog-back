package com.tablelog.tablelogback.domain.chat.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_CHAT")
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_id", nullable = false)
    private String roomId;
    
    @Column(name = "username", nullable = false)
    private String sender;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "message_type", length = 20)
    private String messageType = "TEXT"; // "TEXT", "IMAGE", "FILE" 등

    // 1:1 채팅 수신/읽음 상태 관리를 위한 필드
    @Column(name = "sender_email", length = 255, nullable = false)
    private String senderEmail;

    @Column(name = "receiver_email", length = 255, nullable = false)
    private String receiverEmail;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    public Chat(String roomId, String sender, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.messageType = "TEXT";
    }
}
