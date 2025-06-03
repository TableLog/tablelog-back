package com.tablelog.tablelogback.domain.board_comment.dto.service;

import com.tablelog.tablelogback.global.enums.BoardCategory;
import java.time.LocalDateTime;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

public record BoardCommentReadResponseDto(
        String content,
        String user,
        String profileImgUrl,
        LocalDateTime createdAt
) {

}
