package com.tablelog.tablelogback.domain.recipe_food.service;

import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;
import java.util.List;

public interface RecipeFoodService {
    void createRecipeFood(Long id, RecipeFoodCreateServiceRequestDto requestDto, User user);
    RecipeFoodReadAllServiceResponseDto readRecipeFood(Long rId, Long rfId);
    List<RecipeFoodReadAllServiceResponseDto> readAllRecipeFoodsByRecipeId(Long recipeId);
}
