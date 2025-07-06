package com.tablelog.tablelogback.domain.admin_user.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundAdminUserException extends CustomException {
    public NotFoundAdminUserException(final ErrorCode errorCode){
        super(errorCode);
    }
}
