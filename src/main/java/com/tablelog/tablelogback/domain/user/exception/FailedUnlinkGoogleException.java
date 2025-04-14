package com.tablelog.tablelogback.domain.user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class FailedUnlinkGoogleException extends CustomException {
    public FailedUnlinkGoogleException(ErrorCode errorCode){
        super(errorCode);
    }
}
