package com.tablelog.tablelogback.domain.recipe_payment.dto;

import com.tablelog.tablelogback.global.enums.PaymentMethod;
import com.tablelog.tablelogback.global.enums.PaymentStatus;

import java.time.LocalDateTime;

public record RecipePaymentReadResponseDto(
        Long id,
        Long recipeId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime modifiedAt
) {
}
