package com.tablelog.tablelogback.domain.board.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsBoardException extends CustomException {

    public AlreadyExistsBoardException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
