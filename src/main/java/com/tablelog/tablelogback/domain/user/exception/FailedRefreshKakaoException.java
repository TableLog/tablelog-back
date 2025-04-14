package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class FailedRefreshKakaoException extends CustomException {
    public FailedRefreshKakaoException(ErrorCode errorCode){
        super(errorCode);
    }
}
