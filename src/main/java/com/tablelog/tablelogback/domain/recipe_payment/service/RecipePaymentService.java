package com.tablelog.tablelogback.domain.recipe_payment.service;

import com.tablelog.tablelogback.domain.recipe_payment.dto.RecipePaymentSliceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;

public interface RecipePaymentService {
    void createRecipePayment(Long recipeId, User user);
    RecipePaymentSliceResponseDto getAllMyRecipePayments(User user, int pageNum);
}
