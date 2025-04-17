package com.tablelog.tablelogback.domain.food.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class AlreadyExistsFoodNameException extends CustomException {
    public AlreadyExistsFoodNameException(ErrorCode errorCode){
        super(errorCode);
    }
}
