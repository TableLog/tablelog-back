package com.tablelog.tablelogback.domain.recipe.repository;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Slice<Recipe> findAllByUserId(Long id, PageRequest pageRequest);

    @Query(value = """
        SELECT * FROM tb_recipe r
        WHERE r.id IN (
            SELECT rf.recipe_id
            FROM tb_recipe_food rf
            JOIN tb_food f ON rf.food_id = f.id
            WHERE LOWER(f.food_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """,
            countQuery = """
                    SELECT COUNT(*) FROM tb_recipe r
                    WHERE r.id IN (
                        SELECT rf.recipe_id
                        FROM tb_recipe_food rf
                        JOIN tb_food f ON rf.food_id = f.id
                        WHERE LOWER(f.food_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
                    """,
            nativeQuery = true)
    Slice<Recipe> searchRecipesByFoodName(@Param("keyword") String keyword, PageRequest pageRequest);

    @Query(value = """
        SELECT r.*
        FROM tb_recipe r
        WHERE r.created_at >= :oneWeekAgo
        ORDER BY r.star DESC, r.comment_count DESC, r.created_at DESC
    """, nativeQuery = true)
    Slice<Recipe> findPopularRecipesLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, PageRequest pageRequest);
}
