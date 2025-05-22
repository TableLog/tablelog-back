package com.tablelog.tablelogback.domain.follow.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsFollowingException extends CustomException {
    public AlreadyExistsFollowingException(final ErrorCode errorCode){
        super(errorCode);
    }
}
