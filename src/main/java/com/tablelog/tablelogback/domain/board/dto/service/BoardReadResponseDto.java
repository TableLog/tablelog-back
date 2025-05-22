package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import java.time.LocalDateTime;

public record BoardReadResponseDto(
        Long id,
        String title,
        LocalDateTime created_at,
        String content,
        BoardCategory category,
        String image_url,
        String  profileImgUrl,
        Integer comment_count,
        Long like_count,
        String user
)
{

}
