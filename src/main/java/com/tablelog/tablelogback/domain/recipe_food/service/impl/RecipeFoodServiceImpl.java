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
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodSliceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import com.tablelog.tablelogback.domain.recipe_food.exception.DuplicateRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.ForbiddenAccessRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.NotFoundRecipeFoodException;
import com.tablelog.tablelogback.domain.recipe_food.exception.RecipeFoodErrorCode;
import com.tablelog.tablelogback.domain.recipe_food.mapper.entity.RecipeFoodEntityMapper;
import com.tablelog.tablelogback.domain.recipe_food.repository.RecipeFoodRepository;
import com.tablelog.tablelogback.domain.recipe_food.service.RecipeFoodService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

    @Transactional
    public void createRecipeFood(Long recipeId, final RecipeFoodCreateServiceRequestDto serviceRequestDto, User user) {
        Recipe recipe = findRecipe(recipeId);
        validateRecipeFood(recipe, user);
        Food food = foodRepository.findById(serviceRequestDto.foodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        if(recipeFoodRepository.existsByRecipeIdAndFoodId(recipe.getId(), food.getId())) {
                throw new DuplicateRecipeFoodException(RecipeFoodErrorCode.DUPLICATE_RECIPE_FOOD);
        }
        RecipeFood recipeFood = recipeFoodEntityMapper.toRecipeFood(serviceRequestDto, recipe, food.getId());
        Integer cal = calculateCal(recipeFood.getRecipeFoodUnit(), recipeFood.getAmount(), food);
        recipe.updateTotalCal(recipe.getTotalCal() + cal);
        recipeRepository.save(recipe);
        recipeFoodRepository.save(recipeFood);
    }

    @Override
    public RecipeFoodReadAllServiceResponseDto readRecipeFood(Long recipeId, Long recipeFoodId){
        findRecipe(recipeId);
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        Food food = foodRepository.findById(recipeFood.getFoodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        return recipeFoodEntityMapper.toRecipeFoodReadResponseDto(recipeFood, food);
    }

    @Override
    public RecipeFoodSliceResponseDto readAllRecipeFoodsByRecipeId(Long recipeId, int pageNum){
        Recipe recipe = findRecipe(recipeId);
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<RecipeFood> slice = recipeFoodRepository.findAllByRecipeId(recipe.getId(), pageRequest);
        List<RecipeFoodReadAllServiceResponseDto> recipeFoods = slice.getContent().stream()
                .map(recipeFood -> {
                    Food food = foodRepository.findById(recipeFood.getFoodId())
                            .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
                    return recipeFoodEntityMapper.toRecipeFoodReadResponseDto(recipeFood, food);
                })
                .toList();
        return new RecipeFoodSliceResponseDto(recipeFoods, slice.hasNext());
    }

    @Transactional
    public void updateRecipeFood(
            Long recipeId, Long recipeFoodId,
            RecipeFoodUpdateServiceRequestDto requestDto, User user
    ) throws IOException {
        Recipe recipe = findRecipe(recipeId);
        validateRecipeFood(recipe, user);
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        Food food = findFood(recipeFood.getFoodId());
        Integer beforeCal = calculateCal(recipeFood.getRecipeFoodUnit(), recipeFood.getAmount(), food);
        Integer nowCal = calculateCal(requestDto.recipeFoodUnit(), requestDto.amount(), food);
        recipeFood.updateRecipeFood(requestDto.amount(), requestDto.recipeFoodUnit());
        recipe.updateTotalCal(recipe.getTotalCal() - beforeCal + nowCal);
        recipeRepository.save(recipe);
        recipeFoodRepository.save(recipeFood);
    }

    @Transactional
    public void deleteRecipeFood(Long recipeId, Long recipeFoodId, User user) {
        Recipe recipe = findRecipe(recipeId);
        validateRecipeFood(recipe, user);
        RecipeFood recipeFood = findRecipeFood(recipeFoodId);
        Food food = findFood(recipeFood.getFoodId());
        Integer cal = calculateCal(recipeFood.getRecipeFoodUnit(), recipeFood.getAmount(), food);
        recipe.updateTotalCal(recipe.getTotalCal() - cal);
        recipeRepository.save(recipe);
        recipeFoodRepository.delete(recipeFood);
    }

    private void validateRecipeFood(Recipe recipe, User user){
        if (!Objects.equals(recipe.getUserId(), user.getId()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeFoodException(RecipeFoodErrorCode.FORBIDDEN_ACCESS_RECIPE_FOOD);
        }
    }

    private RecipeFood findRecipeFood(Long recipeFoodId){
        RecipeFood recipeFood = recipeFoodRepository.findById(recipeFoodId)
                .orElseThrow(() -> new NotFoundRecipeFoodException(RecipeFoodErrorCode.NOT_FOUND_RECIPE_FOOD));
        return recipeFood;
    }

    private Recipe findRecipe(Long recipeId){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        return recipe;
    }

    private Food findFood(Long foodId){
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        return food;
    }

    private Integer calculateCal(FoodUnit foodUnit, Integer amount, Food food){
        double userAmountInBase = foodUnit.toBaseUnit(amount);
        double foodUnitToBase = food.getFoodUnit().toBaseUnit(1.0);
        double caloriePerBaseUnit = food.getCal() / foodUnitToBase;
        int cal = (int) (userAmountInBase * caloriePerBaseUnit);
        return cal;
    }
}
