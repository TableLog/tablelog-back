package com.tablelog.tablelogback.domain.recipe_review.dto.service;

public record RecipeReviewUpdateServiceRequestDto(
        String content,
        Float star,
        Long prrId
) {
}
