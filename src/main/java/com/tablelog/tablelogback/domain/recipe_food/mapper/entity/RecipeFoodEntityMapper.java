package com.tablelog.tablelogback.domain.recipe_food.mapper.entity;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeFoodEntityMapper {
    @Mapping(source = "recipe", target = "recipe")
    @Mapping(source = "foodId", target = "foodId")
    RecipeFood toRecipeFood(RecipeFoodCreateServiceRequestDto requestDto, Recipe recipe, Long foodId);
    @Mapping(source = "recipeFood.id", target = "id")
    @Mapping(source = "recipeFood.foodId", target = "foodId")
    @Mapping(source = "food.foodName", target = "foodName")
    @Mapping(source = "food.cal", target = "cal")
    RecipeFoodReadAllServiceResponseDto toRecipeFoodReadResponseDto(RecipeFood recipeFood, Food food);
    List<RecipeFoodReadAllServiceResponseDto> toRecipeFoodReadAllResponseDto(List<RecipeFood> recipeFoods);
}
