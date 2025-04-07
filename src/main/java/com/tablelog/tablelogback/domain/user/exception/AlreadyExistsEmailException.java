package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsEmailException extends CustomException {
    public AlreadyExistsEmailException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
