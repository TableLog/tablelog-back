package com.tablelog.tablelogback.domain.recipe.dto.service;

public record RecipeLikeCountDto(
        Long recipeId,
        Long likeCount
) {
}
