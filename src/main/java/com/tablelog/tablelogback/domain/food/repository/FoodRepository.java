package com.tablelog.tablelogback.domain.food.repository;

import com.tablelog.tablelogback.domain.food.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByFoodName(String foodName);
    Slice<Food> findByIdGreaterThanOrderByIdAsc(Long cursor, Pageable pageable);
    Slice<Food> findByIdGreaterThanAndFoodNameContainingOrderByIdAsc(Long cursor, String foodName, Pageable pageable);
}
