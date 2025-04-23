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
import com.tablelog.tablelogback.domain.recipe_food.exception.ForbiddenAccessRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.NotFoundRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.RecipeFoodErrorCode;
import com.tablelog.tablelogback.domain.recipe_food.mapper.entity.RecipeFoodEntityMapper;
import com.tablelog.tablelogback.domain.recipe_food.repository.RecipeFoodRepository;
import com.tablelog.tablelogback.domain.recipe_food.service.RecipeFoodService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RecipeFoodServiceImpl implements RecipeFoodService {

    private final RecipeFoodRepository recipeFoodRepository;
    private final RecipeFoodEntityMapper recipeFoodEntityMapper;
    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;

    @Override
    public void createRecipeFood(Long recipeId, final RecipeFoodCreateServiceRequestDto serviceRequestDto, User user) {
        validateRecipeFood(recipeId, user);
        Food food = foodRepository.findById(serviceRequestDto.foodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        RecipeFood recipeFood = recipeFoodEntityMapper.toRecipeFood(serviceRequestDto, recipeId, food);
        recipeFoodRepository.save(recipeFood);
    }

    @Override
    public RecipeFoodReadAllServiceResponseDto readRecipeFood(Long recipeId, Long recipeFoodId){
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        return recipeFoodEntityMapper.toRecipeFoodReadResponseDto(recipeFood);
    }

    @Override
    public List<RecipeFoodReadAllServiceResponseDto> readAllRecipeFoodsByRecipeId(Long id){
        List<RecipeFood> recipeFoods = recipeFoodRepository.findAllByRecipeId(id);
        return recipeFoodEntityMapper.toRecipeFoodReadAllResponseDto(recipeFoods);
    }

    @Transactional
    public void updateRecipeFood(
            Long recipeId, Long recipeFoodId,
            RecipeFoodUpdateServiceRequestDto requestDto, User user
    ) throws IOException {
        validateRecipeFood(recipeId, user);
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        recipeFood.updateRecipeFood(requestDto.amount(), requestDto.recipeFoodUnit(),
                recipeFoodId, requestDto.foodId());
        recipeFoodRepository.save(recipeFood);
    }

    @Override
    public void deleteRecipeFood(Long recipeId, Long recipeFoodId, User user) {
        validateRecipeFood(recipeId, user);
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        recipeFoodRepository.delete(recipeFood);
    }

    private void validateRecipeFood(Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        if (!Objects.equals(recipe.getUserId(), user.getId()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeFoodException(RecipeFoodErrorCode.FORBIDDEN_ACCESS_RECIPE_FOOD);
        }
    }

    private RecipeFood findRecipeFood(Long recipeFoodId){
        RecipeFood recipeFood = recipeFoodRepository.findById(recipeFoodId)
                .orElseThrow(() -> new NotFoundRecipeFoodException(RecipeFoodErrorCode.NOT_FOUND_RECIPE_FOOD));
        return recipeFood;
    }
}
