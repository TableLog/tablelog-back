package com.tablelog.tablelogback.domain.food.mapper.entity;

import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.food.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FoodEntityMapper {
    @Mapping(source = "foodUnit",target = "foodUnit")
    Food toFood(FoodCreateServiceRequestDto requestDto);

    FoodReadAllServiceResponseDto toFoodReadResponseDto(Food food);

    List<FoodReadAllServiceResponseDto> toFoodReadAllResponseDto(List<Food> foods);
}
