package com.tablelog.tablelogback.domain.recipe_review.service;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;

public interface RecipeReviewService {
    void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    void createRecipeReply(RecipeReviewReplyCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    RecipeReviewReadResponseDto readRecipeReview(Long recipeId, Long id, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(Long recipeId, int pageNumber, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseDto readAllRecipeReviewsByUser(Long userId, int pageNumber, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseDto getAllMyRecipeReviews(UserDetailsImpl userDetails, int pageNumber);
    void updateRecipeReview(RecipeReviewUpdateServiceRequestDto requestDto, Long recipeId, Long id, User user);
    void deleteRecipeReview(Long recipeId, Long id, User user);
}
