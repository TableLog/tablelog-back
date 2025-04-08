package com.tablelog.tablelogback.domain.board.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import org.springframework.web.multipart.MultipartFile;

public record BoardCreateServiceRequestDto(
        String title,
        String content,
        BoardCategory category,
        MultipartFile image_file
) {

}
