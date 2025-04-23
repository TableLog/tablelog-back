package com.tablelog.tablelogback.domain.recipe_food.repository;

import com.tablelog.tablelogback.domain.recipe_food.entity.RecipeFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeFoodRepository extends JpaRepository<RecipeFood, Long> {
    List<RecipeFood> findAllByRecipeId(Long id);
    Optional<RecipeFood> findByRecipeIdAndId(Long recipeId, Long recipeFoodId);
}
