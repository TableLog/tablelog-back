package com.tablelog.tablelogback.domain.recipe_review.repository;

import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeReviewRepository extends JpaRepository<RecipeReview, Long> {

    Slice<RecipeReview> findAllByUser(String user, Pageable pageable);

    Slice<RecipeReview> findAllByRecipeIdAndPrrId(Long recipeId, Long prrId, Pageable pageable);

    RecipeReview findAllByPrrId(Long prrId);

    List<RecipeReview> findAllByPrrIdIn(List<Long> recipeIds);

    Boolean existsByPrrId(Long prrId);

    Boolean existsByRecipeIdAndUserAndPrrId(Long recipeId, String user, Long prrId);

    List<RecipeReview> findAllByUser(String user);

    void deleteByPrrId(Long prrId);
}
