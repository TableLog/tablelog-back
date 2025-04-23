package com.tablelog.tablelogback.domain.recipe_food.mapper.entity;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_food.dto.service.RecipeFoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeFoodEntityMapper {
    RecipeFood toRecipeFood(RecipeFoodCreateServiceRequestDto requestDto, Long recipeId, Food food);
    RecipeFoodReadAllServiceResponseDto toRecipeFoodReadResponseDto(RecipeFood recipeFood);
    List<RecipeFoodReadAllServiceResponseDto> toRecipeFoodReadAllResponseDto(List<RecipeFood> recipeFoods);
}
