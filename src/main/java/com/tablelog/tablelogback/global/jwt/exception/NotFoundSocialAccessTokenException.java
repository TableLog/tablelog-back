package com.tablelog.tablelogback.global.jwt.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundSocialAccessTokenException extends CustomException {
    public NotFoundSocialAccessTokenException(final ErrorCode errorCode){
        super(errorCode);
    }
}
