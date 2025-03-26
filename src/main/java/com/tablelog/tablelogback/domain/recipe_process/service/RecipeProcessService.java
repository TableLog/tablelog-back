package com.tablelog.tablelogback.domain.recipe_process.service;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.response.RecipeProcessReadAllServiceResponseDto;

import java.io.IOException;

public interface RecipeProcessService {
    void createRecipeProcess(RecipeProcessCreateServiceRequestDto recipeProcessRequestDto,
                             Long recipeId) throws IOException;
    RecipeProcessReadAllServiceResponseDto readRecipeProcess(Long id);
}
