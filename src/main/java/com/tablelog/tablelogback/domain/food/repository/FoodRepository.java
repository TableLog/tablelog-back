package com.tablelog.tablelogback.domain.food.repository;

import com.tablelog.tablelogback.domain.food.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByFoodName(String foodName);
    Slice<Food> findAllBy(Pageable pageable);
    List<Food> findAllByFoodNameContaining(String foodName);
    Slice<Food> findByFoodNameContaining(String foodName, Pageable pageable);
}
