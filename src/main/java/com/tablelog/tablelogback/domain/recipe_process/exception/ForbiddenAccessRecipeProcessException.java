package com.tablelog.tablelogback.domain.recipe_process.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessRecipeProcessException extends CustomException {
    public ForbiddenAccessRecipeProcessException(final ErrorCode errorCode){
        super(errorCode);
    }
}
