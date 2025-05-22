package com.tablelog.tablelogback.domain.recipe_save.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import com.tablelog.tablelogback.domain.recipe_like.exception.NotFoundRecipeLikeException;
import com.tablelog.tablelogback.domain.recipe_like.exception.RecipeLikeErrorCode;
import com.tablelog.tablelogback.domain.recipe_save.entity.RecipeSave;
import com.tablelog.tablelogback.domain.recipe_save.exception.AlreadyExistsRecipeSaveException;
import com.tablelog.tablelogback.domain.recipe_save.exception.NotFoundRecipeSaveException;
import com.tablelog.tablelogback.domain.recipe_save.exception.RecipeSaveErrorCode;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
import com.tablelog.tablelogback.domain.recipe_save.service.RecipeSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeSaveServiceImpl implements RecipeSaveService {
    private final RecipeSaveRepository recipeSaveRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public void createRecipeSave(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        if(recipeSaveRepository.existsByRecipeAndUser(recipe.getId(), userId)){
            throw new AlreadyExistsRecipeSaveException(RecipeSaveErrorCode.ALREADY_EXIST_RECIPE_SAVE);
        }
        RecipeSave save = RecipeSave.builder()
                .user(userId)
                .recipe(recipeId)
                .build();
        recipeSaveRepository.save(save);
    }

    @Override
    public void deleteRecipeSave(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        RecipeSave recipeSave = recipeSaveRepository.findByRecipeAndUser(recipeId, userId)
                .orElseThrow(()->new NotFoundRecipeSaveException(RecipeSaveErrorCode.NOT_FOUND_RECIPE_SAVE));
        recipeSaveRepository.delete(recipeSave);
    }

    private Recipe findRecipe(Long id){
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
    }
}
