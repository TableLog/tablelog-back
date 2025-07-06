package com.tablelog.tablelogback.domain.recipe_review.dto.controller;

public record RecipeReviewReplyCreateControllerRequestDto(
        String content,
        Long prrId
) {
}
