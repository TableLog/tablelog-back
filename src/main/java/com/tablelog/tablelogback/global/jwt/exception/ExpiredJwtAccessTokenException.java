package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ExpiredJwtAccessTokenException extends CustomException {
    public ExpiredJwtAccessTokenException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
