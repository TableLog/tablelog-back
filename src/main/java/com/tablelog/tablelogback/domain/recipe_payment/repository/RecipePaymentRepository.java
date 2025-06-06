package com.tablelog.tablelogback.domain.recipe_payment.repository;

import com.tablelog.tablelogback.domain.recipe_payment.entity.RecipePayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePaymentRepository extends JpaRepository<RecipePayment, Long> {
    Boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    Slice<RecipePayment> findAllByUserId(Long userId, Pageable pageable);
}
