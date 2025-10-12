package com.tablelog.tablelogback.domain.inquiry.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class InvalidInquiryTypeException extends CustomException {
    public InvalidInquiryTypeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
