package com.tablelog.tablelogback.domain.board.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessTestException extends CustomException {

    public ForbiddenAccessTestException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
