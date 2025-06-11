package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.CookingTime;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import com.tablelog.tablelogback.global.enums.RecipePrice;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RecipeUpdateServiceRequestDto(
        String title,
        String intro,
        @ArraySchema(schema = @Schema(implementation = RecipeCategory.class))
        List<RecipeCategory> recipeCategoryList,
        RecipePrice price,
        CookingTime cookingTime,
        Boolean isPaid,
        Integer recipePoint
) {
}
