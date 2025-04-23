package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenCreateFoodException extends CustomException {
    public ForbiddenCreateFoodException(final ErrorCode errorCode){
        super(errorCode);
    }
}
