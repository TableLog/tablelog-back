package com.tablelog.tablelogback.domain.shopping_list.service.impl;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListSliceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.entity.ShoppingList;
import com.tablelog.tablelogback.domain.shopping_list.exception.ForbiddenAccessShoppingListException;
import com.tablelog.tablelogback.domain.shopping_list.exception.NotFoundShoppingListException;
import com.tablelog.tablelogback.domain.shopping_list.exception.ShoppingListErrorCode;
import com.tablelog.tablelogback.domain.shopping_list.mapper.entity.ShoppingListEntityMapper;
import com.tablelog.tablelogback.domain.shopping_list.repository.ShoppingListRepository;
import com.tablelog.tablelogback.domain.shopping_list.service.ShoppingListService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListEntityMapper shoppingListEntityMapper;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Override
    public Long createShoppingList(ShoppingListCreateServiceRequestDto requestDto, User user){
        ShoppingList shoppingList = shoppingListEntityMapper.toShoppingList(requestDto, user.getId());
        shoppingListRepository.save(shoppingList);
        return shoppingList.getId();
    }

    @Override
    public ShoppingListReadAllServiceResponseDto readShoppingList(Long id, User user){
        ShoppingList shoppingList = findShoppingList(id);
        Food food = foodRepository.findById(shoppingList.getFoodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        return shoppingListEntityMapper.toShoppingListReadResponseDto(shoppingList, food.getFoodName());
    }

    @Override
    public ShoppingListSliceResponseDto readAllShoppingListsByUserId(User user, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 20);
        Slice<ShoppingList> slice = shoppingListRepository.findAllByUserId(user.getId(), pageRequest);
        Long listCount = shoppingListRepository.countAllByUserId(user.getId());
        List<ShoppingList> shoppingListEntities = slice.getContent();
        List<Long> foodIds = shoppingListEntities.stream()
                .map(ShoppingList::getFoodId)
                .distinct()
                .toList();
        Map<Long, String> foodIdToNameMap = foodRepository.findAllById(foodIds).stream()
                .collect(Collectors.toMap(Food::getId, Food::getFoodName));
        List<ShoppingListReadAllServiceResponseDto> shoppingLists = shoppingListEntities.stream()
                .map(shoppingList -> {
                    String foodName = foodIdToNameMap.getOrDefault(shoppingList.getFoodId(), "알 수 없음");
                    return shoppingListEntityMapper.toShoppingListReadResponseDto(shoppingList, foodName);
                })
                .toList();
        return new ShoppingListSliceResponseDto(listCount, shoppingLists, slice.hasNext());
    }

    @Override
    public void updateShoppingList(ShoppingListUpdateServiceRequestDto requestDto, Long id, User user){
        ShoppingList shoppingList = findShoppingList(id);
        Food food = foodRepository.findById(shoppingList.getFoodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        if(shoppingList.getUserId() != user.getId()){
            throw new ForbiddenAccessShoppingListException(ShoppingListErrorCode.FORBIDDEN_ACCESS_SHOPPING_LIST);
        }
        shoppingList.updateIsChecked(requestDto.isChecked());
        shoppingListRepository.save(shoppingList);
    }

    @Override
    public void deleteShoppingList(Long id, User user){
        ShoppingList shoppingList = findShoppingList(id);
        if(shoppingList.getUserId() != user.getId()){
            throw new ForbiddenAccessShoppingListException(ShoppingListErrorCode.FORBIDDEN_ACCESS_SHOPPING_LIST);
        }
        shoppingListRepository.delete(shoppingList);
    }

    private ShoppingList findShoppingList(Long id){
        return shoppingListRepository.findById(id)
                .orElseThrow(() -> new NotFoundShoppingListException(ShoppingListErrorCode.NOT_FOUND_SHOPPING_LIST));
    }
}
