package com.tablelog.tablelogback.domain.recipe_review.repository;

import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeReviewRepository extends JpaRepository<RecipeReview, Long> {
    Slice<RecipeReview> findAllByRecipeId(Long recipeId, PageRequest pageRequest);
    Slice<RecipeReview> findAllByUser(String user, PageRequest pageRequest);
    Boolean existsByPrrId(Long prrId);
}
