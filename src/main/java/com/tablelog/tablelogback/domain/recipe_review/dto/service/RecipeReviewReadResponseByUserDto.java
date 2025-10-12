package com.tablelog.tablelogback.domain.recipe_review.dto.service;

import java.time.LocalDateTime;

public record RecipeReviewReadResponseByUserDto(
        Long id,
        String content,
        Float star,
        Long recipeId,
        String user,
        LocalDateTime modifiedAt,
        Long prrId,
        boolean isReviewer,
        String profileImgUrl,
        String recipeTitle,
        String recipeImageUrl,
        RecipeReviewReadResponseByUserDto reply
) {
}
