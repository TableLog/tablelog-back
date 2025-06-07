package com.tablelog.tablelogback.domain.recipe_memo.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_memo.entity.RecipeMemo;
import com.tablelog.tablelogback.domain.recipe_memo.exception.AlreadyExistsRecipeMemoException;
import com.tablelog.tablelogback.domain.recipe_memo.exception.RecipeMemoErrorCode;
import com.tablelog.tablelogback.domain.recipe_memo.repository.RecipeMemoRepository;
import com.tablelog.tablelogback.domain.recipe_memo.service.RecipeMemoService;
import com.tablelog.tablelogback.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeMemoServiceImpl implements RecipeMemoService {
    private final RecipeRepository recipeRepository;
    private final RecipeMemoRepository recipeMemoRepository;

    @Override
    public void createRecipeMemo(Long recipeId, User user, String memo){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        if(recipeMemoRepository.existsByRecipeIdAndUserId(recipeId, user.getId())){
            throw new AlreadyExistsRecipeMemoException(RecipeMemoErrorCode.ALREADY_EXIST_RECIPE_MEMO);
        }
        RecipeMemo recipeMemo = RecipeMemo.builder()
                .recipeId(recipeId)
                .userId(user.getId())
                .memo(memo)
                .build();
        recipeMemoRepository.save(recipeMemo);
    }
}
