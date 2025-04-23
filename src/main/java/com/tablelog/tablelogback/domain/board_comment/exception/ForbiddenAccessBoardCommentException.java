package com.tablelog.tablelogback.domain.board_comment.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessBoardCommentException extends CustomException {

    public ForbiddenAccessBoardCommentException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
