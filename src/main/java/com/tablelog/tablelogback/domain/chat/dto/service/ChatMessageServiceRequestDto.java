package com.tablelog.tablelogback.domain.chat.dto.service;

import java.time.LocalDateTime;

public record ChatMessageServiceRequestDto(
    String roomId,
    String username,
    String message,
    String messageType
) {
}
