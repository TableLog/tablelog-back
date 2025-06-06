package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import java.time.LocalDateTime;
import java.util.List;

public record BoardReadResponseDto(
    Long id,
    String title,
    LocalDateTime createdAt,
    String content,
    BoardCategory category,
    List<String> image_urls,
    String  profileImgUrl,
    Integer comment_count,
    Long like_count,
    String user,
    Boolean isLike,
    Boolean isMe
)
{

}
