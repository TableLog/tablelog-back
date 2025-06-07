package com.tablelog.tablelogback.domain.recipe_memo.repository;

import com.tablelog.tablelogback.domain.recipe_memo.entity.RecipeMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeMemoRepository extends JpaRepository<RecipeMemo, Long> {
    boolean existsByRecipeIdAndUserId(Long recipeId, Long userId);
    Optional<RecipeMemo> findByRecipeIdAndUserId(Long recipeId, Long userId);
}
