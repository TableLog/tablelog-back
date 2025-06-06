package com.tablelog.tablelogback.domain.recipe_payment.dto;

import java.util.List;

public record RecipePaymentSliceResponseDto(
        List<RecipePaymentReadResponseDto> contents,
        boolean hasNext
) {
}
