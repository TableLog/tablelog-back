package com.tablelog.tablelogback.domain.inquiry.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundInquiryException extends CustomException {
    public NotFoundInquiryException(final ErrorCode errorCode){
        super(errorCode);
    }
}
