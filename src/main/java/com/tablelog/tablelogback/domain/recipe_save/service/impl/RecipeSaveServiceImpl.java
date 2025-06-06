package com.tablelog.tablelogback.domain.recipe_save.service.impl;

import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.mapper.entity.RecipeEntityMapper;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_save.entity.RecipeSave;
import com.tablelog.tablelogback.domain.recipe_save.exception.AlreadyExistsRecipeSaveException;
import com.tablelog.tablelogback.domain.recipe_save.exception.NotFoundRecipeSaveException;
import com.tablelog.tablelogback.domain.recipe_save.exception.RecipeSaveErrorCode;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
import com.tablelog.tablelogback.domain.recipe_save.service.RecipeSaveService;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeSaveServiceImpl implements RecipeSaveService {
    private final RecipeSaveRepository recipeSaveRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeEntityMapper recipeEntityMapper;
    private final RecipeLikeRepository recipeLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRecipeSave(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        if(recipeSaveRepository.existsByRecipeAndUser(recipe.getId(), userId)){
            throw new AlreadyExistsRecipeSaveException(RecipeSaveErrorCode.ALREADY_EXIST_RECIPE_SAVE);
        }
        RecipeSave save = RecipeSave.builder()
                .user(userId)
                .recipe(recipeId)
                .build();
        recipeSaveRepository.save(save);
    }

    @Override
    public void deleteRecipeSave(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        RecipeSave recipeSave = recipeSaveRepository.findByRecipeAndUser(recipeId, userId)
                .orElseThrow(()->new NotFoundRecipeSaveException(RecipeSaveErrorCode.NOT_FOUND_RECIPE_SAVE));
        recipeSaveRepository.delete(recipeSave);
    }

    @Override
    public Boolean hasRecipeSaved(Long recipeId, Long userId){
        Recipe recipe = findRecipe(recipeId);
        return recipeSaveRepository.existsByRecipeAndUser(recipeId, userId);
    }

    @Override
    public RecipeSliceResponseDto getMySavedRecipes(UserDetailsImpl userDetails, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeSaveRepository.findAllByUser(userDetails.user().getId(), pageRequest);

        List<Long> recipeIds = slice.getContent().stream()
                .map(Recipe::getId)
                .collect(Collectors.toList());

        List<Long> userIds = slice.getContent().stream()
                .map(Recipe::getUserId)
                .distinct()
                .toList();

        Map<Long, String> userIdToNickname = userRepository.findNicknamesByUserIds(userIds).stream()
                .collect(Collectors.toMap(RecipeUserNicknameDto::userId, RecipeUserNicknameDto::nickname));

        Map<Long, Long> likeCountMap = recipeLikeRepository.countLikesByRecipeIds(recipeIds).stream()
                .collect(Collectors.toMap(RecipeLikeCountDto::recipeId, RecipeLikeCountDto::likeCount));

        Long userId = userDetails.user().getId();

        List<RecipeReadAllServiceResponseDto> recipes = slice.getContent().stream()
                .map(recipe -> {
                    Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                    String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                    Boolean isWriter = userId.equals(recipe.getUserId());
                    return recipeEntityMapper.toRecipeReadResponseDto(recipe, likeCount, true, nickname, isWriter);
                })
                .collect(Collectors.toList());
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    private Recipe findRecipe(Long id){
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
    }
}
