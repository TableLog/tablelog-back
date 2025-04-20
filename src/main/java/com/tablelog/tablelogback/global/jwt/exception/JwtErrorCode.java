package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    // 400
    FAILED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "EJ400001"),

    // 401
    EXPIRED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EJ401001"),
    EXPIRED_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EJ401002"),

    // 404
    NOT_FOUND_JWT(HttpStatus.NOT_FOUND, "EJ404001"),
    NOT_FOUND_SOCIAL_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "EJ404002");

    private final HttpStatus status;
    private final String message;
}
