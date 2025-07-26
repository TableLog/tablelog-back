package com.tablelog.tablelogback.domain.recipe_review.service;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;

public interface RecipeReviewService {
    void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    void createRecipeReply(RecipeReviewReplyCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);
    RecipeReviewReadResponseDto readRecipeReview(Long recipeId, Long id, Boolean includeReplies, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(Long recipeId, int pageNumber, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseByUserDto readAllRecipeReviewsByUser(Long userId, int pageNumber, UserDetailsImpl userDetails);
    RecipeReviewSliceResponseByUserDto getAllMyRecipeReviews(UserDetailsImpl userDetails, int pageNumber);
    void updateRecipeReview(RecipeReviewUpdateServiceRequestDto requestDto, Long recipeId, Long id, User user);
    void deleteRecipeReview(Long recipeId, Long id, User user);
}
