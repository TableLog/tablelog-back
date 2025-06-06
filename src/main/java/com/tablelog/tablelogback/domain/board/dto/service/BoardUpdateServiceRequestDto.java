package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import java.util.List;

public record BoardUpdateServiceRequestDto(
        String title,
        String content,
        BoardCategory category,
        List<String> image_urls
) {
}
