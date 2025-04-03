package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotMatchPasswordException extends CustomException {
    public NotMatchPasswordException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
