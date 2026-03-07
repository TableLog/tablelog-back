package com.tablelog.tablelogback.domain.recipe_food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class DuplicateRecipeFoodException extends CustomException {
    public DuplicateRecipeFoodException(final ErrorCode errorCode){
        super(errorCode);
    }
}
