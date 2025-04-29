package com.tablelog.tablelogback.domain.food.service.impl;

import com.tablelog.tablelogback.domain.food.dto.service.request.FoodCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.request.FoodUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.food.dto.service.response.FoodSliceResponseDto;
import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.*;
import com.tablelog.tablelogback.domain.food.mapper.entity.FoodEntityMapper;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.food.service.FoodService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FoodServiceImpl implements FoodService {
    private final FoodEntityMapper foodEntityMapper;
    private final FoodRepository foodRepository;

    @Override
    public void createFood(FoodCreateServiceRequestDto requestDto, User user) throws IOException {
        if(user.getUserRole() != UserRole.ADMIN){
            throw new ForbiddenCreateFoodException(FoodErrorCode.FORBIDDEN_CREATE_FOOD);
        }

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
    public FoodSliceResponseDto readAllFoods(Integer pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Slice<Food> slice = foodRepository.findAll(pageRequest);
        List<FoodReadAllServiceResponseDto> foods =
                foodEntityMapper.toFoodReadAllResponseDto(slice.getContent());
        return new FoodSliceResponseDto(foods, slice.hasNext());
    }

    @Override
    public FoodSliceResponseDto searchFoods(String keyword, Integer pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Slice<Food> slice = foodRepository.findByFoodNameContaining(keyword, pageRequest);
        List<FoodReadAllServiceResponseDto> foods =
                foodEntityMapper.toFoodReadAllResponseDto(slice.getContent());
        return new FoodSliceResponseDto(foods, slice.hasNext());
    }

    @Transactional
    public void updateFood(Long id, FoodUpdateServiceRequestDto requestDto, User user) throws IOException {
        if(user.getUserRole() != UserRole.ADMIN){
            throw new ForbiddenUpdateFoodException(FoodErrorCode.FORBIDDEN_UPDATE_FOOD);
        }
        Food food = foodRepository.findById(id)
                .orElseThrow(()->new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        food.updateFood(requestDto.foodName(), requestDto.foodUnit(), requestDto.cal());
        foodRepository.save(food);
    }

    @Override
    public void deleteFood(Long id, User user) {
        if(user.getUserRole() != UserRole.ADMIN){
            throw new ForbiddenDeleteFoodException(FoodErrorCode.FORBIDDEN_DELETE_FOOD);
        }
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        foodRepository.delete(food);
    }
}
