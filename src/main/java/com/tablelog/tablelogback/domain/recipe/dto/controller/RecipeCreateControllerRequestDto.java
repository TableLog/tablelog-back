package com.tablelog.tablelogback.domain.recipe.dto.controller;

import com.tablelog.tablelogback.global.enums.CookingTime;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import com.tablelog.tablelogback.global.enums.RecipePrice;

import java.util.List;

public record RecipeCreateControllerRequestDto(
        String title,
        String intro,
        List<RecipeCategory> recipeCategoryList,
        RecipePrice price,
        CookingTime cookingTime,
        String memo,
        Boolean isPaid,
        Integer recipePoint
) {
}
