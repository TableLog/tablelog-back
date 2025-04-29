package com.tablelog.tablelogback.domain.recipe.service;

import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_process.dto.service.RecipeProcessCreateRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RecipeService {
    void createRecipe(RecipeCreateServiceRequestDto requestDto, MultipartFile recipeImage,
                      List<RecipeFoodCreateServiceRequestDto> rfRequestDtos,
                      RecipeProcessCreateRequestDto rpRequestDtos,
                      User user
    ) throws IOException;
    RecipeReadAllServiceResponseDto readRecipe(Long id);
    RecipeSliceResponseDto readAllRecipes(int pageNum);
    RecipeSliceResponseDto readAllRecipeByUser(Long userId, int pageNum);
    RecipeSliceResponseDto readAllRecipeByFoodName(String keyword, int pageNum);
    RecipeSliceResponseDto filterRecipes(RecipeFilterConditionDto condition, int pageNum);
    void updateRecipe(Long id, RecipeUpdateServiceRequestDto requestDto, User user,
                      MultipartFile multipartFile) throws IOException;
    void deleteRecipe(Long id, User user);
}
