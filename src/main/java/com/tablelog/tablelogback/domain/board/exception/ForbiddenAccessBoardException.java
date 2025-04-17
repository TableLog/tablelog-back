package com.tablelog.tablelogback.domain.board.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessBoardException extends CustomException {

    public ForbiddenAccessBoardException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
