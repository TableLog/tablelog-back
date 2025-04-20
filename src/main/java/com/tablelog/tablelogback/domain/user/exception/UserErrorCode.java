package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "EU400001"),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "EU400002"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "EU400003"),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "EU400004"),
    MATCH_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "EU400005"),
    BAD_LOGIN(HttpStatus.BAD_REQUEST, "EU400006"),
    FAILED_UNLINK_KAKAO(HttpStatus.BAD_REQUEST, "EU400007"),
    FAILED_UNLINK_GOOGLE(HttpStatus.BAD_REQUEST, "EU400008"),
    FAILED_REFRESH_KAKAO(HttpStatus.BAD_REQUEST, "EU400009"),
    FAILED_REFRESH_GOOGLE(HttpStatus.BAD_REQUEST, "EU400010"),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "EU400011"),

    //403
    NOT_ADMIN(HttpStatus.FORBIDDEN, "EU403001"),

    // 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "EU404001"),
    NOT_FOUND_KAKAO_USER(HttpStatus.NOT_FOUND, "EU404002"),
    NOT_FOUND_GOOGLE_USER(HttpStatus.NOT_FOUND, "EU404003");

    private final HttpStatus status;
    private final String message;
}
