package com.tablelog.tablelogback.domain.board.dto.service;

import java.util.List;

public record BoardAllStatisticDto(
        Long totalCount,
        List<BoardStatisticDto> dailyCounts
) {
}
