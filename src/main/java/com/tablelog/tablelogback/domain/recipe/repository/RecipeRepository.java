package com.tablelog.tablelogback.domain.recipe.repository;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Slice<Recipe> findAllByUserId(Long id, PageRequest pageRequest);

    @Query(value = """
        SELECT * FROM tb_recipe r
        WHERE r.id IN (
            SELECT rf.recipe_id
            FROM tb_recipe_food rf
            JOIN tb_food f ON rf.food_id = f.id
            WHERE f.food_name LIKE %:keyword%
        )
        """,
            countQuery = """
        SELECT COUNT(*) FROM tb_recipe r
        WHERE r.id IN (
            SELECT rf.recipe_id
            FROM tb_recipe_food rf
            JOIN tb_food f ON rf.food_id = f.id
            WHERE f.food_name LIKE %:keyword%
        )
        """,
            nativeQuery = true)
    Slice<Recipe> searchRecipesByFoodName(@Param("keyword") String keyword, PageRequest pageRequest);
}
