package com.tablelog.tablelogback.domain.board.dto.controller;

import com.tablelog.tablelogback.global.enums.BoardCategory;

public record BoardCreateControllerRequestDto(
        String title,
        String content,
        BoardCategory category
) {

}
