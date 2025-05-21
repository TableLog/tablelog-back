package com.tablelog.tablelogback.domain.recipe_like.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import com.tablelog.tablelogback.domain.recipe_like.exception.AlreadyExistsRecipeLikeException;
import com.tablelog.tablelogback.domain.recipe_like.exception.NotFoundRecipeLikeException;
import com.tablelog.tablelogback.domain.recipe_like.exception.RecipeLikeErrorCode;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_like.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeLikeServiceImpl implements RecipeLikeService {
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public void createRecipeLike(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        if(recipeLikeRepository.existsByRecipeAndUser(recipe.getId(), userId)){
            throw new AlreadyExistsRecipeLikeException(RecipeLikeErrorCode.ALREADY_EXIST_RECIPE_LIKE);
        }
        RecipeLike like = RecipeLike.builder()
                .user(userId)
                .recipe(recipeId)
                .build();
        recipeLikeRepository.save(like);
    }

    @Override
    public void deleteRecipeLike(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        RecipeLike recipeLike = recipeLikeRepository.findByRecipeAndUser(recipeId, userId)
                .orElseThrow(()->new NotFoundRecipeLikeException(RecipeLikeErrorCode.NOT_FOUND_RECIPE_LIKE));
        recipeLikeRepository.delete(recipeLike);
    }

    private Recipe findRecipe(Long id){
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
    }
}
