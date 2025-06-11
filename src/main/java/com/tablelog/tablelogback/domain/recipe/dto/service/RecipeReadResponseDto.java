package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.CookingTime;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import com.tablelog.tablelogback.global.enums.RecipePrice;

import java.util.List;

public record RecipeReadResponseDto(
        Long id,
        String title,
        String intro,
        List<RecipeCategory> recipeCategoryList,
        int starCount,
        Float star,
        RecipePrice price,
        CookingTime cookingTime,
        Integer totalCal,
        Boolean isPaid,
        Integer recipePoint,
        Integer reviewCount,
        String imageUrl,
        Long likeCount,
        Boolean isSaved,
        String user,
        Boolean isWriter,
        Boolean hasPurchased
) {
}
