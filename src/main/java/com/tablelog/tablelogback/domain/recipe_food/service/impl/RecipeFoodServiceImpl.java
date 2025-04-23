package com.tablelog.tablelogback.domain.recipe_food.service.impl;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import com.tablelog.tablelogback.domain.recipe_food.exception.NotFoundRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.RecipeFoodErrorCode;
import com.tablelog.tablelogback.domain.recipe_food.mapper.entity.RecipeFoodEntityMapper;
import com.tablelog.tablelogback.domain.recipe_food.repository.RecipeFoodRepository;
import com.tablelog.tablelogback.domain.recipe_food.service.RecipeFoodService;
import com.tablelog.tablelogback.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeFoodServiceImpl implements RecipeFoodService {

    private final RecipeFoodRepository recipeFoodRepository;
    private final RecipeFoodEntityMapper recipeFoodEntityMapper;
    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;

    @Override
    public void createRecipeFood(Long recipeId, final RecipeFoodCreateServiceRequestDto serviceRequestDto, User user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        Food food = foodRepository.findById(serviceRequestDto.foodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        RecipeFood recipeFood = recipeFoodEntityMapper.toRecipeFood(serviceRequestDto, recipe, food);
        recipeFoodRepository.save(recipeFood);
    }

    @Override
    public RecipeFoodReadAllServiceResponseDto readRecipeFood(Long recipeId, Long recipeFoodId){
        RecipeFood recipeFood = recipeFoodRepository.findByRecipeIdAndId(recipeId, recipeFoodId)
                .orElseThrow(() -> new NotFoundRecipeFoodException(RecipeFoodErrorCode.NOT_FOUND_RECIPE_FOOD));
        return recipeFoodEntityMapper.toRecipeFoodReadResponseDto(recipeFood);
    }
}
