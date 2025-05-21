package com.tablelog.tablelogback.domain.follow.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class SelfFollowNotAllowedException extends CustomException {
    public SelfFollowNotAllowedException(final ErrorCode errorCode){
        super(errorCode);
    }
}
