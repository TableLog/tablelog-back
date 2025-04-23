package com.tablelog.tablelogback.domain.food.service;

import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;
import java.util.List;

public interface FoodService {
    void createFood (FoodCreateServiceRequestDto requestDto, User user) throws IOException;
    FoodReadAllServiceResponseDto readFood(Long id);
    List<FoodReadAllServiceResponseDto> readAllFoods(Integer pageNumber);
    List<FoodReadAllServiceResponseDto> searchFoods(String keyword, Integer page);
    void updateFood (Long id, FoodUpdateServiceRequestDto requestDto, User user) throws IOException;
    void deleteFood(Long id, User user);
}
