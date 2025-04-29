package com.tablelog.tablelogback.domain.recipe_food.service;

import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodSliceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;

public interface RecipeFoodService {
    void createRecipeFood(Long id, RecipeFoodCreateServiceRequestDto requestDto, User user);
    RecipeFoodReadAllServiceResponseDto readRecipeFood(Long rId, Long rfId);
    RecipeFoodSliceResponseDto readAllRecipeFoodsByRecipeId(Long recipeId, int pageNum);
    void updateRecipeFood (Long rId, Long rfId, RecipeFoodUpdateServiceRequestDto requestDto,
                           User user) throws IOException;
    void deleteRecipeFood(Long rId, Long rfId, User user);
}
