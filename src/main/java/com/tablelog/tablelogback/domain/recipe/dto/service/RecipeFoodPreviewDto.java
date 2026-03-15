package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodPreviewDto(
        Long id,
        Double amount,
        FoodUnit recipeFoodUnit,
        Long foodId,
        String foodName,
//        Double cal,
        Boolean isChecked,
        Long shoppingListId
) {
}
