package com.tablelog.tablelogback.domain.recipe_review.repository;

import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeReviewRepository extends JpaRepository<RecipeReview, Long> {
    Slice<RecipeReview> findAllByRecipeId(Long recipeId, Pageable pageable);
    Slice<RecipeReview> findAllByUser(String user, Pageable pageable);
    Boolean existsByPrrId(Long prrId);
    void deleteByPrrId(Long prrId);
}
