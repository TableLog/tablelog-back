package com.tablelog.tablelogback.domain.recipe_payment.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipePaymentErrorCode implements ErrorCode {
    // 400
    ALREADY_EXISTS_RECIPE_PAYMENT(HttpStatus.BAD_REQUEST, "ERPP400001"),
    CAN_NOT_PAYMENT_RECIPE(HttpStatus.BAD_REQUEST, "ERPP400002");

    private final HttpStatus status;
    private final String message;
}
