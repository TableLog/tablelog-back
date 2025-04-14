package com.tablelog.tablelogback.domain.food.mapper.dto;

import com.tablelog.tablelogback.domain.food.dto.controller.FoodCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.food.dto.controller.FoodUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FoodDtoMapper {
    FoodCreateServiceRequestDto toFoodCreateServiceDto(
            FoodCreateControllerRequestDto controllerRequestDto
    );

    FoodUpdateServiceRequestDto toFoodUpdateServiceDto(
            FoodUpdateControllerRequestDto controllerRequestDto
    );
}
