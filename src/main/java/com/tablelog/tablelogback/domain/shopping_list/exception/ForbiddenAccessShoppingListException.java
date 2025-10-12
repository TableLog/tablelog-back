package com.tablelog.tablelogback.domain.shopping_list.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class ForbiddenAccessShoppingListException extends CustomException {
    public ForbiddenAccessShoppingListException(final ErrorCode errorCode){
        super(errorCode);
    }
}
