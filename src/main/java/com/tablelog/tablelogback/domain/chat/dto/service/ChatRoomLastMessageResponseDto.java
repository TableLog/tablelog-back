package com.tablelog.tablelogback.domain.chat.dto.service;

import java.time.LocalDateTime;

public record ChatRoomLastMessageResponseDto(
    String roomId,
    String lastMessage,
    LocalDateTime lastCreatedAt,
    long unreadCount
) {
}


