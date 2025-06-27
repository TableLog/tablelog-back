package com.tablelog.tablelogback.domain.recipe_process.service;

import com.tablelog.tablelogback.domain.recipe_process.dto.service.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RecipeProcessService {
    void createRecipeProcess(Long rId, RecipeProcessCreateServiceRequestDto serviceRequestDto,
                             List<MultipartFile> recipeProcessImages, User user) throws IOException;
    RecipeProcessReadAllServiceResponseDto readRecipeProcess(Long rId, Long rfId);
    RecipeProcessSliceResponseDto readRecipeProcessWithSequence(Long rId, Long s);
    RecipeProcessReadAllSliceResponseDto readAllRecipeProcessesByRecipeId(Long recipeId, int page);
    void updateRecipeProcess(Long rId, Long rfId, RecipeProcessUpdateServiceRequestDto requestDto,
                             List<MultipartFile> recipeProcessImages, User user) throws IOException;
    void deleteRecipeProcess(Long rId, Long rfId, User user);
}
