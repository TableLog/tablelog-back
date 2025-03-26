package com.tablelog.tablelogback.domain.recipe_process.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import com.tablelog.tablelogback.domain.recipe_process.mapper.entity.RecipeProcessEntityMapper;
import com.tablelog.tablelogback.domain.recipe_process.repository.RecipeProcessRepository;
import com.tablelog.tablelogback.domain.recipe_process.service.RecipeProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class RecipeProcessServiceImpl implements RecipeProcessService {
    private final RecipeRepository recipeRepository;
    private final RecipeProcessRepository recipeProcessRepository;
    private final RecipeProcessEntityMapper recipeProcessEntityMapper;

    public void createRecipeProcess(
            RecipeProcessCreateServiceRequestDto requestDto,
//            User user,
            Long recipeId
//            MultipartFile multipartFile
    ) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow();
        String imgUrl = "";
        RecipeProcess recipeProcess = recipeProcessEntityMapper.toRecipeProcess(requestDto, imgUrl, recipe);
        recipeProcessRepository.save(recipeProcess);
    }
}
