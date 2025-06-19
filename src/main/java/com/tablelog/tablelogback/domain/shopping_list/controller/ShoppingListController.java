package com.tablelog.tablelogback.domain.shopping_list.controller;

import com.tablelog.tablelogback.domain.recipe_review.dto.controller.RecipeReviewUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.controller.ShoppingListCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListReadAllServiceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.dto.service.ShoppingListSliceResponseDto;
import com.tablelog.tablelogback.domain.shopping_list.mapper.dto.ShoppingListDtoMapper;
import com.tablelog.tablelogback.domain.shopping_list.service.impl.ShoppingListServiceImpl;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(name = "장보기 목록 API", description = "")
public class ShoppingListController {
    private final ShoppingListDtoMapper shoppingListDtoMapper;
    private final ShoppingListServiceImpl shoppingListService;

    @Operation(summary = "장보기 목록 생성")
    @PostMapping("/shopping-list")
    public ResponseEntity<?> createShoppingList(
            @RequestBody ShoppingListCreateControllerRequestDto controllerRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ShoppingListCreateServiceRequestDto serviceRequestDto =
                shoppingListDtoMapper.toShoppingListCreateServiceDto(controllerRequestDto);
        shoppingListService.createShoppingList(serviceRequestDto, userDetails.user());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
