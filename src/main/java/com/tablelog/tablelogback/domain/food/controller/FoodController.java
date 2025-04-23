package com.tablelog.tablelogback.domain.food.controller;

import com.tablelog.tablelogback.domain.food.dto.controller.FoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.food.dto.controller.FoodUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.food.mapper.dto.FoodDtoMapper;
import com.tablelog.tablelogback.domain.food.service.impl.FoodServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "식재료 API", description = "")
public class FoodController {
    private final FoodDtoMapper foodDtoMapper;
    private final FoodServiceImpl foodService;

    @Operation(summary = "식재료 생성")
    @PostMapping("/foods")
    public ResponseEntity<?> createFood(
            @RequestBody FoodCreateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        FoodCreateServiceRequestDto serviceRequestDto =
                foodDtoMapper.toFoodCreateServiceDto(controllerRequestDto);
        foodService.createFood(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "식재료 단건 조회")
    @GetMapping("/foods/{foodId}")
    public ResponseEntity<FoodReadAllServiceResponseDto> readFood(
            @PathVariable Long foodId
    ){
        FoodReadAllServiceResponseDto responseDto = foodService.readFood(foodId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "식재료 전체 조회 (페이징 + 검색)")
    @GetMapping("/foods")
    public ResponseEntity<List<FoodReadAllServiceResponseDto>> readAllFoods(
            @RequestParam(required = false) String search,
            @RequestParam(name = "page", required = false) Integer page
    ){
        if (search != null && !search.isBlank()) {
            if (page != null) {
                return ResponseEntity.ok(foodService.searchFoods(search, page)); // 검색 + 페이징
            }
            else {
                return ResponseEntity.ok(foodService.searchFoods(search, -1)); // 전체 검색
            }
        }

        if (page != null) {
            return ResponseEntity.ok(foodService.readAllFoods(page)); // 페이징
        }
        else {
            return ResponseEntity.ok(foodService.readAllFoods(-1)); // 전체 조회
        }
    }

    @Operation(summary = "식재료 수정")
    @PutMapping("/foods/{foodId}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long foodId,
            @RequestBody FoodUpdateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        FoodUpdateServiceRequestDto serviceRequestDto = foodDtoMapper.toFoodUpdateServiceDto(controllerRequestDto);
        foodService.updateFood(foodId, serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "식재료 삭제")
    @DeleteMapping("/foods/{foodId}")
    public ResponseEntity<?> deleteFood(
            @PathVariable Long foodId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        foodService.deleteFood(foodId, userDetails.user());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
