package com.tablelog.tablelogback.domain.board_comment.dto.service;

import java.util.List;

public record BoardCommentListResponseDto(
        List<BoardCommentReadResponseDto> boardComments,
        boolean hasNext
) {}