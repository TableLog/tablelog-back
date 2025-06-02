package com.tablelog.tablelogback.domain.recipe_payment.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class CannotPaymentRecipeException extends CustomException {
    public CannotPaymentRecipeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
