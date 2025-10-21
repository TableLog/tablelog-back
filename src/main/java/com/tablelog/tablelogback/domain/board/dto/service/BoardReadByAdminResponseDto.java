package com.tablelog.tablelogback.domain.board.dto.service;

import java.time.LocalDateTime;

public record BoardReadByAdminResponseDto(
        Long id,
        String writer,
        String user,
        LocalDateTime createdAt,
        String content
) {
}
