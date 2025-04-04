package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundUserException extends CustomException {
    public NotFoundUserException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
