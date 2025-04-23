package com.tablelog.tablelogback.domain.recipe.exception;

import com.tablelog.tablelogback.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeErrorCode implements ErrorCode{
    // 403
    FORBIDDEN_CREATE_FOOD(HttpStatus.FORBIDDEN, "식재료를 생성할 권한이 없습니다."),
    FORBIDDEN_UPDATE_FOOD(HttpStatus.FORBIDDEN, "식재료를 수정할 권한이 없습니다."),
    FORBIDDEN_DELETE_FOOD(HttpStatus.FORBIDDEN, "식재료를 삭제할 권한이 없습니다."),
    ALREADY_EXISTS_FOOD_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 식재료입니다."),

    // 404
    NOT_FOUND_RECIPE(HttpStatus.NOT_FOUND, "식재료를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
