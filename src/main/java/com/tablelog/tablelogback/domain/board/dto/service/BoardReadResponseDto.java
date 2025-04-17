package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;

public record BoardReadResponseDto(
        String title,
        String content,
        BoardCategory category,
        String image_url,
        String user
) {

}
