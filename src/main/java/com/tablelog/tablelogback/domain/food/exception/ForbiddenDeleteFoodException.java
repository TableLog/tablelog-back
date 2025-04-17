package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenDeleteFoodException extends CustomException {
    public ForbiddenDeleteFoodException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
