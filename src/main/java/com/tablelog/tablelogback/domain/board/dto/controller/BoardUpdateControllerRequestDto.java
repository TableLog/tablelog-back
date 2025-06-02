package com.tablelog.tablelogback.domain.board.dto.controller;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import java.util.List;

public record BoardUpdateControllerRequestDto(
        String title,
        String content,
        BoardCategory category,
        List<String> image_urls
) {
}
