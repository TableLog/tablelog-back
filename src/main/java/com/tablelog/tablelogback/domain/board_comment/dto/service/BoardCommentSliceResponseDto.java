package com.tablelog.tablelogback.domain.board_comment.dto.service;

import java.util.List;

public record BoardCommentSliceResponseDto(
         List<BoardCommentReadResponseDto> contents,
         boolean hasNext
) {
}
