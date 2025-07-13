package com.tablelog.tablelogback.domain.user_license.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundUserLicenseException extends CustomException {
    public NotFoundUserLicenseException(final ErrorCode errorCode){
        super(errorCode);
    }
}
