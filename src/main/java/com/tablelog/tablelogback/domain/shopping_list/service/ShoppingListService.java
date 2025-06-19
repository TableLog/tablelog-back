package com.tablelog.tablelogback.domain.shopping_list.service;

import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListSliceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;

public interface ShoppingListService {
    void createShoppingList(ShoppingListCreateServiceRequestDto requestDto, User user);
//    ShoppingListReadAllServiceResponseDto readShoppingList(Long id, User user);
//    ShoppingListSliceResponseDto readAllShoppingListsByUserId(User user, int pageNum);
//    void updateShoppingList(ShoppingListUpdateServiceRequestDto requestDto, User user);
//    void deleteShoppingList(User user);
}
