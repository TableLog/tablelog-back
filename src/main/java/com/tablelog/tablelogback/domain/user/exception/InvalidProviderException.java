package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class InvalidProviderException extends CustomException {
    public InvalidProviderException(final ErrorCode errorCode){
        super(errorCode);
    }
}
