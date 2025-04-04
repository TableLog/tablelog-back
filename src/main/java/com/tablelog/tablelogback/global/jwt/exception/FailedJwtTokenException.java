package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class FailedJwtTokenException extends CustomException {
    public FailedJwtTokenException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
