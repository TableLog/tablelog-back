package com.tablelog.tablelogback.domain.chat.dto.service;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomSummaryResponseDto {
    private String roomId;
    private LocalDateTime lastCreatedAt;
    private long messageCount;
}


