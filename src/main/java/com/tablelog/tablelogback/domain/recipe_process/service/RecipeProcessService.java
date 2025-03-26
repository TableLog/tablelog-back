package com.tablelog.tablelogback.domain.recipe_process.service;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.request.RecipeProcessUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.response.RecipeProcessReadAllServiceResponseDto;

import java.io.IOException;
import java.util.List;

public interface RecipeProcessService {
    void createRecipeProcess(RecipeProcessCreateServiceRequestDto recipeProcessRequestDto,
                             Long recipeId) throws IOException;
    RecipeProcessReadAllServiceResponseDto readRecipeProcess(Long id);
    List<RecipeProcessReadAllServiceResponseDto> readAllRecipeProcesses();
    List<RecipeProcessReadAllServiceResponseDto> readAllRecipeProcessesByRecipeId(Long id);
    void updateRecipeProcess(Long id, RecipeProcessUpdateServiceRequestDto requestDto
//                            , MultipartFile multipartFile, User user
                             ) throws IOException;
}
