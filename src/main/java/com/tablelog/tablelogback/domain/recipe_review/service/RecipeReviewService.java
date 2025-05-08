package com.tablelog.tablelogback.domain.recipe_review.service;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewSliceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface RecipeReviewService {
    void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    RecipeReviewReadResponseDto readRecipeReview(Long recipeId, Long id);
    RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(Long recipeId, int pageNumber);
}
