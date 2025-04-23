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
    @Mapping(source = "recipe.id", target = "recipeId")
    RecipeFood toRecipeFood(RecipeFoodCreateServiceRequestDto requestDto, Recipe recipe, Food food);
    RecipeFoodReadAllServiceResponseDto toRecipeFoodReadResponseDto(RecipeFood recipeFood);
    List<RecipeFoodReadAllServiceResponseDto> toRecipeFoodReadAllResponseDto(List<RecipeFood> recipeFoods);
}
