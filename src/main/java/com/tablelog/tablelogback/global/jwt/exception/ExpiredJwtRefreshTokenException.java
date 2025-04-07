package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ExpiredJwtRefreshTokenException extends CustomException {
    public ExpiredJwtRefreshTokenException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
