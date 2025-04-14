package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundFoodException extends CustomException {
    public NotFoundFoodException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
