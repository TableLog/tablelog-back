package com.tablelog.tablelogback.domain.recipe_like.repository;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    Optional<RecipeLike> findByRecipeAndUser(Long user, Long recipe);
    @Query("SELECT r FROM Recipe r JOIN RecipeLike l ON r.id = l.recipe " +
            "WHERE l.user = :userId")
    Slice<Recipe> findAllByUser(@Param("userId") Long userId, PageRequest pageRequest);
    Boolean existsByRecipeAndUser(Long user, Long recipe);
    Long countByRecipe(Long recipe);
}
