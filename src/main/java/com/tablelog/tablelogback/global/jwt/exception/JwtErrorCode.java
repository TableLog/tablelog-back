package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    // 401 (요구사항: 인증 실패는 401로 통일)
    FAILED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "EJ400001"),

    // 401
    EXPIRED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EJ401001"),
    EXPIRED_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EJ401002"),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "EJ401001"),

    // 404
    NOT_FOUND_JWT(HttpStatus.NOT_FOUND, "EJ404001"),
    NOT_FOUND_SOCIAL_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "EJ404002"),
    NOT_FOUND_SOCIAL_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "EJ404003");

    private final HttpStatus status;
    private final String message;
}
