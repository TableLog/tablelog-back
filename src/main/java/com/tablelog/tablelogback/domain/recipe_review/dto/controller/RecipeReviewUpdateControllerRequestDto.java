package com.tablelog.tablelogback.domain.recipe_review.dto.controller;

public record RecipeReviewUpdateControllerRequestDto(
        String content,
        Float star,
        Long prrId
) {
}
