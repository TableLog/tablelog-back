package com.tablelog.tablelogback.domain.follow.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_FOLLOWING(HttpStatus.BAD_REQUEST, "EFF400001"),
    CAN_NOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "EFF400002"),

    // 404
    NOT_FOUND_FOLLOWING(HttpStatus.NOT_FOUND, "EFF404001");

    private final HttpStatus status;
    private final String message;
}
