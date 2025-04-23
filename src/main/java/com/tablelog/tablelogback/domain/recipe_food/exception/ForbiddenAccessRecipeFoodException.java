package com.tablelog.tablelogback.domain.recipe_food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessRecipeFoodException extends CustomException {
    public ForbiddenAccessRecipeFoodException(final ErrorCode errorCode){
        super(errorCode);
    }
}
