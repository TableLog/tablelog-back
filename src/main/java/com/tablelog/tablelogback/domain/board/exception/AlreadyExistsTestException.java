package com.tablelog.tablelogback.domain.board.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsTestException extends CustomException {

    public AlreadyExistsTestException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
