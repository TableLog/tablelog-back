package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.CookingTime;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import com.tablelog.tablelogback.global.enums.RecipePrice;

import java.util.List;

public record RecipeFilterConditionDto(
        List<RecipeCategory> recipeCategory,
        CookingTime cookingTime,
        Integer cal,
        RecipePrice recipePrice
) {
}
