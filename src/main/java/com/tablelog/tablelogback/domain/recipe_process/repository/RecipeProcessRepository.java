package com.tablelog.tablelogback.domain.recipe_process.repository;

import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Long> {
    Slice<RecipeProcess> findAllByRecipeId(Long id, Pageable pageable);
    Optional<RecipeProcess> findByRecipeIdAndId(Long recipeId, Long recipeProcessId);
    void deleteAllByRecipeId(Long recipeId);
}
