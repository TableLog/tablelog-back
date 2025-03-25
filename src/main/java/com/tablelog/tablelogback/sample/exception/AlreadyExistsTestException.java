package com.tablelog.tablelogback.sample.exception;


import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsTestException extends CustomException {

    public AlreadyExistsTestException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
