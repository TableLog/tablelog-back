package com.tablelog.tablelogback.domain.food.service.impl;

import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.AlreadyExistsFoodNameException;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.ForbiddenDeleteFoodException;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.mapper.entity.FoodEntityMapper;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.food.service.FoodService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FoodServiceImpl implements FoodService {
    private final FoodEntityMapper foodEntityMapper;
    private final FoodRepository foodRepository;

    @Override
    public void createFood(FoodCreateServiceRequestDto requestDto) throws IOException {
        if(foodRepository.existsByFoodName(requestDto.foodName())){
            throw new AlreadyExistsFoodNameException(FoodErrorCode.ALREADY_EXISTS_FOOD_NAME);
        }
        Food food = foodEntityMapper.toFood(requestDto);
        foodRepository.save(food);
    }

    @Override
    public FoodReadAllServiceResponseDto readFood(Long id){
        Food food = foodRepository.findById(id)
                .orElseThrow(()->new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        return foodEntityMapper.toFoodReadResponseDto(food);
    }

    @Override
    public List<FoodReadAllServiceResponseDto> readAllFoods(Integer pageNumber) {
        if(pageNumber < 0){
            List<Food> foods = foodRepository.findAll();
            return foodEntityMapper.toFoodReadAllResponseDto(foods);
        } else {
            Slice<Food> foods = foodRepository.findAllBy(PageRequest.of(pageNumber, 9));
            return foodEntityMapper.toFoodReadAllResponseDto(foods.getContent());
        }
    }

    @Override
    public List<FoodReadAllServiceResponseDto> searchFoods(String keyword, Integer pageNumber) {
        if(pageNumber < 0){
            List<Food> foods = foodRepository.findAllByFoodNameContaining(keyword);
            return foodEntityMapper.toFoodReadAllResponseDto(foods);
        } else {
            Slice<Food> foods = foodRepository.findByFoodNameContaining(keyword, PageRequest.of(pageNumber, 9));
            return foodEntityMapper.toFoodReadAllResponseDto(foods.getContent());
        }
    }

    @Transactional
    public void updateFood(Long id, FoodUpdateServiceRequestDto requestDto) throws IOException {
        Food food = foodRepository.findById(id)
                .orElseThrow(()->new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));

        // foodName
        if(requestDto.foodName() != null && !Objects.equals(requestDto.foodName(), "")){
            food.updateFoodName(requestDto.foodName());
        }

        // foodUnit
        if(requestDto.foodUnit() != null && !Objects.equals(requestDto.foodUnit(), "")){
            food.updateFoodUnit(requestDto.foodUnit());
        }
        
        // cal
        if(requestDto.cal() != null && requestDto.cal() > 0){
            food.updateCal(requestDto.cal());
        }
        foodRepository.save(food);
    }
}
