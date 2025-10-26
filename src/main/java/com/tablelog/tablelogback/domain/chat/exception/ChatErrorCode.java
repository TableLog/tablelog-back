package com.tablelog.tablelogback.domain.chat.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    // 400
    INVALID_MESSAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 메시지입니다"),
    
    // 401
    UNAUTHORIZED_CHAT(HttpStatus.UNAUTHORIZED, "채팅 권한이 없습니다"),
    
    // 404
    NOT_FOUND_CHAT(HttpStatus.NOT_FOUND, "채팅 메시지를 찾을 수 없습니다"),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다");

    private final HttpStatus status;
    private final String message;
}
