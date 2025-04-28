package com.tablelog.tablelogback.domain.recipe.repository;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByUserId(Long id);
}
