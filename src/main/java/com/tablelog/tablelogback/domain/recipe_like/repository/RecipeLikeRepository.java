package com.tablelog.tablelogback.domain.recipe_like.repository;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeLikeCountDto;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    Optional<RecipeLike> findByRecipeAndUser(Long recipe, Long user);
    @Query("SELECT r FROM Recipe r JOIN RecipeLike l ON r.id = l.recipe " +
            "WHERE l.user = :userId")
    Slice<Recipe> findAllByUserLatest(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT r FROM Recipe r JOIN RecipeLike l ON r.id = l.recipe " +
            "WHERE l.user = :userId AND r.isPaid = true")
    Slice<Recipe> findAllByUserLatestAndIsPaidTrue(@Param("userId") Long userId, Pageable pageable);
    Boolean existsByRecipeAndUser(Long recipe, Long user);
    Long countByRecipe(Long recipe);
    @Query("SELECT new com.tablelog.tablelogback.domain.recipe.dto.service.RecipeLikeCountDto(l.recipe, COUNT(l)) " +
            "FROM RecipeLike l WHERE l.recipe IN :recipeIds GROUP BY l.recipe")
    List<RecipeLikeCountDto> countLikesByRecipeIds(@Param("recipeIds") List<Long> recipeIds);
}
