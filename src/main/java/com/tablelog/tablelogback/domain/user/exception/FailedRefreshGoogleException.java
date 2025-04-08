package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class FailedRefreshGoogleException extends CustomException {
    public FailedRefreshGoogleException(ErrorCode errorCode){
        super(errorCode);
    }
}
