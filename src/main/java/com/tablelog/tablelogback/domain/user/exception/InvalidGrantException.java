package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class InvalidGrantException extends CustomException {
    public InvalidGrantException(final ErrorCode errorCode){
        super(errorCode);
    }
}
