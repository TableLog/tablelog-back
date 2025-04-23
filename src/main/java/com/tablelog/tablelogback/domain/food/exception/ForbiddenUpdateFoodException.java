package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenUpdateFoodException extends CustomException {
    public ForbiddenUpdateFoodException(final ErrorCode errorCode){
        super(errorCode);
    }
}
