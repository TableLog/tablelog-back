package com.tablelog.tablelogback.domain.inquiry.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessInquiryException extends CustomException {
    public ForbiddenAccessInquiryException(final ErrorCode errorCode){
        super(errorCode);
    }
}
