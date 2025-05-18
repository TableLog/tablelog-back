package com.tablelog.tablelogback.domain.board_like.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardLikeErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_BOARD_LIKE(HttpStatus.BAD_REQUEST, "EL400001");

    private final HttpStatus status;
    private final String message;
}
