package com.tablelog.tablelogback.domain.recipe_like.service;

public interface RecipeLikeService {
    void createRecipeLike(Long recipeId, Long userId);
    void deleteRecipeLike(Long recipeId, Long userId);
    Boolean hasRecipeLiked(Long recipeId, Long userId);
}
