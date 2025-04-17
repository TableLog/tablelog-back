package com.tablelog.tablelogback.domain.board.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundBoardException extends CustomException {

    public NotFoundBoardException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
