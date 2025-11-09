package com.tablelog.tablelogback.domain.board_comment.dto.service;

import java.time.LocalDateTime;

public record BoardCommentReadResponseDto(
        String content,
        String user,
        String profileImgUrl,
        LocalDateTime createdAt,
        Long id,
        Long commentId,
        String comment_count
) {

}
