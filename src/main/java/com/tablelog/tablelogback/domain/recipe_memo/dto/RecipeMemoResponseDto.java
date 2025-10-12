package com.tablelog.tablelogback.domain.recipe_memo.dto;

public record RecipeMemoResponseDto(
        Long id,
        Long recipeId,
        Long userId,
        String memo
) {
}
