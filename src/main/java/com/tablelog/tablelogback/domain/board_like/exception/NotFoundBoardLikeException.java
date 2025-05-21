package com.tablelog.tablelogback.domain.board_like.exception;

import com.tablelog.tablelogback.global.exception.CustomException;
import com.tablelog.tablelogback.global.exception.ErrorCode;

public class NotFoundBoardLikeException extends CustomException {
    public NotFoundBoardLikeException(final ErrorCode errorCode){
        super(errorCode);
    }
}
