package com.tablelog.tablelogback.domain.food.service;

import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface FoodService {
    void createFood (FoodCreateServiceRequestDto requestDto) throws IOException;
    FoodReadAllServiceResponseDto readFood(Long id);
}
