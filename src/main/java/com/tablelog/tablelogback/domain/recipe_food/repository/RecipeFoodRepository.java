package com.tablelog.tablelogback.domain.recipe_food.repository;

import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
    Slice<RecipeFood> findAllByRecipeId(Long id, Pageable pageable);
    Optional<RecipeFood> findByRecipeIdAndId(Long recipeId, Long recipeFoodId);
    void deleteAllByRecipeId(Long recipeId);
}
