package com.tablelog.tablelogback.domain.board_comment.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;

public record BoardCommentReadResponseDto(
        String content,
        String user
) {

}
