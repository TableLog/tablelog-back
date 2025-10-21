package com.tablelog.tablelogback.domain.board.dto.service;

import java.util.List;

public record BoardReadSliceByAdminDto(
        List<BoardReadByAdminResponseDto> contents,
        boolean hasNext
) {
}
