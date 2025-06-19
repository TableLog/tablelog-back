package com.tablelog.tablelogback.domain.shopping_list.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShoppingListErrorCode implements ErrorCode {
    // 404
    NOT_FOUND_SHOPPING_LIST(HttpStatus.NOT_FOUND, "ESL404001");

    private final HttpStatus status;
    private final String message;
}
