package com.tablelog.tablelogback.domain.recipe_process.service;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;

import java.io.IOException;

public interface RecipeProcessService {
    void createRecipeProcess(RecipeProcessCreateServiceRequestDto recipeProcessRequestDto,
                             Long recipeId) throws IOException;
}
