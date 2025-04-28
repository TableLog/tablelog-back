package com.tablelog.tablelogback.domain.recipe_process.repository;

import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Long> {
    List<RecipeProcess> findAllByRecipeId(Long id);
    Optional<RecipeProcess> findByRecipeIdAndId(Long recipeId, Long recipeProcessId);
    void deleteAllByRecipeId(Long recipeId);
}
