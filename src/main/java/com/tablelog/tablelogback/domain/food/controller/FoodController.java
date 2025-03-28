package com.tablelog.tablelogback.domain.food.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "식재료 API", description = "")
public class FoodController {
    @PostMapping("/foods")
    public void createFood(
            String name,
            FoodUnit foodUnit,
            Integer cal
    ) throws IOException {

    }

    @GetMapping("/foods/{foodId}")
    public Map<String, Object> readFood(
            @PathVariable Long foodId
    ){
        Map<String, Object> food1 = new HashMap<>();
        food1.put("name", "사과");
        food1.put("foodUnit", "g");
        food1.put("cal", 300);
        food1.put("user", "user1");
        return food1;
    }

    @GetMapping("/foods")
    public List<Map<String, Object>> readAllFoods(){
        List<Map<String, Object>> foods = new ArrayList<>();
        Map<String, Object> food1 = new HashMap<>();
        food1.put("name", "사과");
        food1.put("foodUnit", "g");
        food1.put("cal", 300);
        food1.put("user", "user1");
        Map<String, Object> food2 = new HashMap<>();
        food2.put("name", "사과");
        food2.put("foodUnit", "g");
        food2.put("cal", 300);
        food2.put("user", "user1");
        foods.add(food1);;
        foods.add(food2);
        return foods;
    }

    @PutMapping("/foods/{foodId}")
    public void updateFood(
            @PathVariable Long foodId,
            String name,
            FoodUnit foodUnit,
            Integer cal
    ) throws IOException {

    }

    @DeleteMapping("/foods/{foodId}")
    public void deleteFood(
            @PathVariable Long foodId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

    }
}
