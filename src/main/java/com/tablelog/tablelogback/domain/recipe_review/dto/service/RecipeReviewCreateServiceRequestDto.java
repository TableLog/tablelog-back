package com.tablelog.tablelogback.domain.recipe_review.dto.service;

public record RecipeReviewCreateServiceRequestDto(
        String content,
        Float star
) {
}
