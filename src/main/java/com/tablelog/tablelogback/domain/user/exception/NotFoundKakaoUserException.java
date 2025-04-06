package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundKakaoUserException  extends CustomException {
    public NotFoundKakaoUserException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
