package com.tablelog.tablelogback.domain.recipe_process.repository;

import com.tablelog.tablelogback.domain.recipe_process.entity.RecipeProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Long> {
}
