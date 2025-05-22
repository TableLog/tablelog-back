package com.tablelog.tablelogback.domain.recipe_save.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsRecipeSaveException extends CustomException {
    public AlreadyExistsRecipeSaveException(final ErrorCode errorCode){
        super(errorCode);
    }
}
