package com.tablelog.tablelogback.domain.recipe_like.repository;

import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    Optional<RecipeLike> findByRecipeAndUser(Long user, Long recipe);
    Boolean existsByRecipeAndUser(Long user, Long recipe);
}
