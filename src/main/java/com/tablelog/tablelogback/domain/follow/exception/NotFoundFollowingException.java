package com.tablelog.tablelogback.domain.follow.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundFollowingException extends CustomException {
    public NotFoundFollowingException(final ErrorCode errorCode){
        super(errorCode);
    }
}
