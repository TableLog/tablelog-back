package com.tablelog.tablelogback.domain.recipe_payment.repository;

import com.tablelog.tablelogback.domain.recipe_payment.entity.RecipePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePaymentRepository extends JpaRepository<RecipePayment, Long> {
    Boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
}
