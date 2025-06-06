package com.tablelog.tablelogback.domain.recipe_like.service.impl;

import com.tablelog.tablelogback.domain.recipe.dto.service.*;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.mapper.entity.RecipeEntityMapper;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_like.entity.RecipeLike;
import com.tablelog.tablelogback.domain.recipe_like.exception.AlreadyExistsRecipeLikeException;
import com.tablelog.tablelogback.domain.recipe_like.exception.NotFoundRecipeLikeException;
import com.tablelog.tablelogback.domain.recipe_like.exception.RecipeLikeErrorCode;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_like.service.RecipeLikeService;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
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
public class RecipeLikeServiceImpl implements RecipeLikeService {
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeEntityMapper recipeEntityMapper;
    private final RecipeSaveRepository recipeSaveRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRecipeLike(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        if(recipeLikeRepository.existsByRecipeAndUser(recipe.getId(), userId)){
            throw new AlreadyExistsRecipeLikeException(RecipeLikeErrorCode.ALREADY_EXIST_RECIPE_LIKE);
        }
        RecipeLike like = RecipeLike.builder()
                .user(userId)
                .recipe(recipeId)
                .build();
        recipeLikeRepository.save(like);
    }

    @Override
    public void deleteRecipeLike(Long recipeId, Long userId) {
        Recipe recipe = findRecipe(recipeId);
        RecipeLike recipeLike = recipeLikeRepository.findByRecipeAndUser(recipeId, userId)
                .orElseThrow(()->new NotFoundRecipeLikeException(RecipeLikeErrorCode.NOT_FOUND_RECIPE_LIKE));
        recipeLikeRepository.delete(recipeLike);
    }

    @Override
    public Boolean hasRecipeLiked(Long recipeId, Long userId){
        Recipe recipe = findRecipe(recipeId);
        return recipeLikeRepository.existsByRecipeAndUser(recipeId, userId);
    }

    @Override
    public Long getRecipeLikeCountByRecipe(Long recipeId) {
        Recipe recipe = findRecipe(recipeId);
        return recipeLikeRepository.countByRecipe(recipeId);
    }

    @Override
    public RecipeSliceResponseDto getMyLikedRecipes(UserDetailsImpl userDetails, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Recipe> slice = recipeLikeRepository.findAllByUser(userDetails.user().getId(), pageRequest);
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

        final Map<Long, Boolean> isSavedMap = (userDetails != null)
                ? recipeSaveRepository.findSavesByRecipeAndUser(recipeIds, userDetails.user().getId())
                .stream()
                .collect(Collectors.toMap(
                        RecipeIsSavedDto::recipeId,
                        RecipeIsSavedDto::isSaved
                ))
                : Collections.emptyMap();

        Long userId = (userDetails != null) ? userDetails.user().getId() : null;

        List<RecipeReadAllServiceResponseDto> recipes = slice.getContent().stream()
                .map(recipe -> {
                    Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                    Boolean isSaved = isSavedMap.getOrDefault(recipe.getId(), false);
                    String nickname = userIdToNickname.getOrDefault(recipe.getUserId(), "Unknown");
                    Boolean isWriter = userId != null && userId.equals(recipe.getUserId());
                    return recipeEntityMapper.toRecipeReadResponseDto(recipe, likeCount, isSaved, nickname, isWriter);
                })
                .collect(Collectors.toList());
        return new RecipeSliceResponseDto(recipes, slice.hasNext());
    }

    private Recipe findRecipe(Long id){
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
    }
}
