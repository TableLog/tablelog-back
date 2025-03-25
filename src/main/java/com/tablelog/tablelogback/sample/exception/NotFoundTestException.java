package com.tablelog.tablelogback.sample.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundTestException extends CustomException {

    public NotFoundTestException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
