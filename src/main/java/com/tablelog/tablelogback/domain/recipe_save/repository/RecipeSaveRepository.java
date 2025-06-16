package com.tablelog.tablelogback.domain.recipe_save.repository;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeIsSavedDto;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe_save.entity.RecipeSave;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecipeSaveRepository extends JpaRepository<RecipeSave, Long> {
    Boolean existsByRecipeAndUser(Long recipe, Long user);
    Optional<RecipeSave> findByRecipeAndUser(Long recipe, Long user);
    @Query("SELECT r FROM Recipe r JOIN RecipeSave s ON r.id = s.recipe " +
            "WHERE s.user = :userId")
    Slice<Recipe> findAllByUser(@Param("userId") Long userId, Pageable pageable);
    @Query("SELECT r FROM Recipe r JOIN RecipeSave s ON r.id = s.recipe " +
            "WHERE s.user = :userId AND r.isPaid = true")
    Slice<Recipe> findAllByUserLatestAndIsPaidTrue(@Param("userId") Long userId, Pageable pageable);
    @Query("""
        SELECT r
        FROM Recipe r
        JOIN RecipeSave s ON r.id = s.recipe
        WHERE s.user = :userId
        ORDER BY r.star DESC, r.reviewCount DESC, r.createdAt DESC
    """)
    Slice<Recipe> findAllByUserPopular(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Long userId, Pageable pageable);
    @Query("""
        SELECT r
        FROM Recipe r
        JOIN RecipeSave s ON r.id = s.recipe
        WHERE s.user = :userId AND r.isPaid = true
        ORDER BY r.star DESC, r.reviewCount DESC, r.createdAt DESC
    """)
    Slice<Recipe> findAllByUserPopularAndIsPaidTrue(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Long userId, Pageable pageable);
    @Query("""
        SELECT new com.tablelog.tablelogback.domain.recipe.dto.service.RecipeIsSavedDto(
            s.recipe,
            COUNT(s) > 0
        )
        FROM RecipeSave s
        WHERE s.recipe IN :recipeIds AND s.user = :userId
        GROUP BY s.recipe
    """)
    List<RecipeIsSavedDto> findSavesByRecipeAndUser(@Param("recipeIds") List<Long> recipeIds,
                                                    @Param("userId") Long userId);
}
