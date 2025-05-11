package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundOAuthAccountException extends CustomException {
    public NotFoundOAuthAccountException(final ErrorCode errorCode){
        super(errorCode);
    }
}
