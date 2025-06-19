package com.tablelog.tablelogback.domain.shopping_list.service.impl;

import com.tablelog.tablelogback.domain.food.entity.Food;
import com.tablelog.tablelogback.domain.food.exception.FoodErrorCode;
import com.tablelog.tablelogback.domain.food.exception.NotFoundFoodException;
import com.tablelog.tablelogback.domain.food.repository.FoodRepository;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListSliceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.entity.ShoppingList;
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

@RequiredArgsConstructor
@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListEntityMapper shoppingListEntityMapper;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Override
    public void createShoppingList(ShoppingListCreateServiceRequestDto requestDto, User user){
        ShoppingList shoppingList = shoppingListEntityMapper.toShoppingList(requestDto, user.getId());
        shoppingListRepository.save(shoppingList);
    }

    @Override
    public ShoppingListReadAllServiceResponseDto readShoppingList(Long id, User user){
        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundShoppingListException(ShoppingListErrorCode.NOT_FOUND_SHOPPING_LIST));
        Food food = foodRepository.findById(shoppingList.getFoodId())
                .orElseThrow(() -> new NotFoundFoodException(FoodErrorCode.NOT_FOUND_FOOD));
        return shoppingListEntityMapper.toShoppingListReadResponseDto(shoppingList, food.getFoodName());
    }
//
//    @Override
//    public ShoppingListSliceResponseDto readAllShoppingListsByUserId(User user, int pageNum){
//        PageRequest pageRequest = PageRequest.of(pageNum, 20);
//        Slice<ShoppingList> slice = shoppingListRepository.findAllByUserId(user.getId(), pageRequest);
//        Long listCount = shoppingListRepository.countAllByUserId(user.getId());
//        List<ShoppingListReadAllServiceResponseDto> shoppingLists =
//                shoppingListEntityMapper.toShoppingListReadAllResponseDto(slice.getContent());
//        return new ShoppingListSliceResponseDto(listCount, shoppingLists, slice.hasNext());
//    }
//
//    @Override
//    public void updateShoppingList(Long id, User user){
//        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(id, user.getId())
//                .orElseThrow(() -> new NotFoundShoppingListException(ShoppingListErrorCode.NOT_FOUND_SHOPPING_LIST));
//        shoppingListRepository.save(shoppingList);
//    }
}
