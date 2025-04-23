package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsUserException extends CustomException {
    public AlreadyExistsUserException(final ErrorCode errorCode){
        super(errorCode);
    }
}
