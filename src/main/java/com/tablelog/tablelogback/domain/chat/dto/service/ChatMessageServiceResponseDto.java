package com.tablelog.tablelogback.domain.chat.dto.service;

import java.time.LocalDateTime;

public record ChatMessageServiceResponseDto(
    Long id,
    String roomId,
    String username,
    String message,
    String messageType,
    LocalDateTime createdAt
) {
}
