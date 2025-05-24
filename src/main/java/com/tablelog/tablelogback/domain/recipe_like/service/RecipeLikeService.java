package com.tablelog.tablelogback.domain.recipe_like.service;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeSliceResponseDto;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;

public interface RecipeLikeService {
    void createRecipeLike(Long recipeId, Long userId);
    void deleteRecipeLike(Long recipeId, Long userId);
    Boolean hasRecipeLiked(Long recipeId, Long userId);
    Long getRecipeLikeCountByRecipe(Long recipeId);
    RecipeSliceResponseDto getMyLikedRecipes(UserDetailsImpl userDetails, int pageNum);
}
