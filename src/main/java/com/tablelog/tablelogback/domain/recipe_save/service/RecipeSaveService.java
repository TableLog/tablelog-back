package com.tablelog.tablelogback.domain.recipe_save.service;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeSliceResponseDto;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;

public interface RecipeSaveService {
    void createRecipeSave(Long recipeId, Long userId);
    void deleteRecipeSave(Long recipeId, Long userId);
    Boolean hasRecipeSaved(Long recipeId, Long userId);
    RecipeSliceResponseDto readMySavedRecipesLatest(Boolean isPaid, UserDetailsImpl userDetails, int pageNum);
    RecipeSliceResponseDto readMySavedRecipesPopular(Boolean isPaid, UserDetailsImpl userDetails, int pageNum);
}
