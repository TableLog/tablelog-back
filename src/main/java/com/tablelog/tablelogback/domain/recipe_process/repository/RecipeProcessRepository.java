package com.tablelog.tablelogback.domain.recipe_process.repository;

import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Long> {
//    Optional<TravelVisitorCafe> findByIdAndUser(Long id, User user);
    List<RecipeProcess> findAllByRecipeId(Long id);
}
