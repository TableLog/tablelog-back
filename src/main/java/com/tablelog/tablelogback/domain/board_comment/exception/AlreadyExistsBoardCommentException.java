package com.tablelog.tablelogback.domain.board_comment.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsBoardCommentException extends CustomException {

    public AlreadyExistsBoardCommentException(final ErrorCode errorCode)    {
        super(errorCode);
    }
}
