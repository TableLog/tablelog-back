package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;

public record BoardCreateServiceRequestDto(
        String title,
        String content,
        BoardCategory category
)
{

}
