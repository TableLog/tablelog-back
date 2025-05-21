package com.tablelog.tablelogback.domain.recipe_like.service;

public interface RecipeLikeService {
    void createRecipeLike(Long recipeId, Long userId);
    void deleteRecipeLike(Long boardId, Long userId);
}
