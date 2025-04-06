package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class FailedUnlinkKakaoException extends CustomException {
    public FailedUnlinkKakaoException(final ErrorCode errorCode){
        super(errorCode);
    }
}
