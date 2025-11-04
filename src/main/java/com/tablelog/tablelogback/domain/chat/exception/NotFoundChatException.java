package com.tablelog.tablelogback.domain.chat.exception;

import com.tablelog.tablelogback.global.exception.CustomException;

public class NotFoundChatException extends CustomException {
    public NotFoundChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}

