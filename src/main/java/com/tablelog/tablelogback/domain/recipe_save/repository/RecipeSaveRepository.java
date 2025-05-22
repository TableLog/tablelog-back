package com.tablelog.tablelogback.domain.recipe_save.repository;

import com.tablelog.tablelogback.domain.recipe_save.entity.RecipeSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeSaveRepository extends JpaRepository<RecipeSave, Long> {
    Boolean existsByRecipeAndUser(Long user, Long recipe);
    Optional<RecipeSave> findByRecipeAndUser(Long user, Long recipe);
}
