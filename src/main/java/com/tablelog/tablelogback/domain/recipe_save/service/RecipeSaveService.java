package com.tablelog.tablelogback.domain.recipe_save.service;

public interface RecipeSaveService {
    void createRecipeSave(Long recipeId, Long userId);
    void deleteRecipeSave(Long recipeId, Long userId);
    Boolean hasRecipeSaved(Long recipeId, Long userId);
}
