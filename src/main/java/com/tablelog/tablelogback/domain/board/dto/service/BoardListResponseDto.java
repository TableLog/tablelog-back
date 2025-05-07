package com.tablelog.tablelogback.domain.board.dto.service;

import java.util.List;

public record BoardListResponseDto(
        List<BoardReadResponseDto> boards,
        boolean hasNext
) {}