package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundSocialRefreshTokenException extends CustomException {
    public NotFoundSocialRefreshTokenException(final ErrorCode errorCode){
        super(errorCode);
    }
}
