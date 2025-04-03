package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    // 400
    FAILED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "토큰 검증에 실패했습니다."),

    // 401
    EXPIRED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 AccessToken입니다."),
    EXPIRED_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 RefreshToken입니다."),

    // 404
    NOT_FOUND_JWT(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
