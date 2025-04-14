package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    // 400
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    MATCH_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호입니다."),
    BAD_LOGIN(HttpStatus.BAD_REQUEST, "로그인에 실패하였습니다."),
    FAILED_UNLINK_KAKAO(HttpStatus.BAD_REQUEST, "카카오 동의 철회에 실패하였습니다."),
    FAILED_UNLINK_GOOGLE(HttpStatus.BAD_REQUEST, "구글 동의 철회에 실패하였습니다."),
    FAILED_REFRESH_KAKAO(HttpStatus.BAD_REQUEST, "카카오 토큰 갱신에 실패하였습니다."),
    FAILED_REFRESH_GOOGLE(HttpStatus.BAD_REQUEST, "구글 토큰 갱신에 실패하였습니다."),

    // 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_KAKAO_USER(HttpStatus.NOT_FOUND, "사용자 카카오 계정을 찾을 수 없습니다."),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
