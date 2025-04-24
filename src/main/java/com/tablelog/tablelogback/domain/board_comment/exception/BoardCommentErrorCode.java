package com.tablelog.tablelogback.domain.board_comment.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardCommentErrorCode implements ErrorCode {

    // 400
    ALREADY_EXIST_BOARDCOMMENT(HttpStatus.BAD_REQUEST, "이미 게시글의 댓글이 존재합니다."),

    // 403
    FORBIDDEN_ACCESS_BOARDCOMMENT(HttpStatus.FORBIDDEN, "게시글의 댓글에 접근할 수 없습니다."),

    // 404
    NOT_FOUND_BOARDCOMMENT(HttpStatus.NOT_FOUND, "게시글의 댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
