package com.tablelog.tablelogback.domain.recipe_process.service;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RecipeProcessService {
    void createRecipeProcess(Long rId, RecipeProcessCreateServiceRequestDto serviceRequestDto,
                             List<MultipartFile> recipeProcessImages, User user) throws IOException;
    RecipeProcessReadAllServiceResponseDto readRecipeProcess(Long rId, Long rfId);
    List<RecipeProcessReadAllServiceResponseDto> readAllRecipeProcessesByRecipeId(Long recipeId);
}
