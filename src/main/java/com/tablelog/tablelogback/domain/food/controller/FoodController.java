package com.tablelog.tablelogback.domain.food.controller;

import com.tablelog.tablelogback.global.enums.FoodUnit;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public void readFood(
            @PathVariable Long foodId
    ){

    }

    @GetMapping("/foods")
    public void readAllFoods(){

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
