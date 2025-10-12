package com.tablelog.tablelogback.domain.recipe.dto.service;

import com.tablelog.tablelogback.global.enums.FoodUnit;

public record RecipeFoodPreviewDto(
        Long id,
        Integer amount,
        FoodUnit recipeFoodUnit,
        Long foodId,
        String foodName,
        Integer cal,
        Boolean isChecked
) {
}
