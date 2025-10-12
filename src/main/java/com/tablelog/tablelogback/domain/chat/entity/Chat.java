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
    private String username;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "message_type", length = 20)
    private String messageType = "TEXT"; // "TEXT", "IMAGE", "FILE" 등
    
    public Chat(String roomId, String username, String message) {
        this.roomId = roomId;
        this.username = username;
        this.message = message;
        this.messageType = "TEXT";
    }
}
