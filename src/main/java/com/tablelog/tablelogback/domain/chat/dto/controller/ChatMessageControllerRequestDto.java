package com.tablelog.tablelogback.domain.chat.dto.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageControllerRequestDto(
    @NotBlank(message = "룸 ID는 필수입니다")
    String roomId,
    
    @NotBlank(message = "메시지는 필수입니다")
    String message,
    
    String messageType
) {
}
