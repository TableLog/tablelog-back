package com.tablelog.tablelogback.domain.recipe_review.service.impl;

import com.tablelog.tablelogback.domain.recipe.dto.service.RecipeSliceResponseDto;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewSliceResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import com.tablelog.tablelogback.domain.recipe_review.exception.ForbiddenAccessRecipeReviewException;
import com.tablelog.tablelogback.domain.recipe_review.exception.NotFoundRecipeReviewException;
import com.tablelog.tablelogback.domain.recipe_review.exception.RecipeReviewErrorCode;
import com.tablelog.tablelogback.domain.recipe_review.mapper.entity.RecipeReviewEntityMapper;
import com.tablelog.tablelogback.domain.recipe_review.repository.RecipeReviewRepository;
import com.tablelog.tablelogback.domain.recipe_review.service.RecipeReviewService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RecipeReviewServiceImpl implements RecipeReviewService {
    private final RecipeReviewRepository recipeReviewRepository;
    private final RecipeReviewEntityMapper recipeReviewEntityMapper;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Override
    public void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReview(serviceRequestDto, recipeId, user);
        recipeReviewRepository.save(recipeReview);
    }

    @Override
    public RecipeReviewReadResponseDto readRecipeReview(Long recipeId, Long id) {
        RecipeReview recipeReview = findRecipeReview(recipeId, id);
        return recipeReviewEntityMapper.toRecipeReviewReadResponseDto(recipeReview);
    }

    @Override
    public RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(Long recipeId, int pageNumber) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));

        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByRecipeId(recipe.getId(), pageRequest);

        List<RecipeReviewReadResponseDto> recipeReviews =
                recipeReviewEntityMapper.toRecipeReviewReadAllResponseDtoLists(slice.getContent());
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext());
    }

    private RecipeReview validateRecipeReview(Long recipeId, Long id, User user){
        RecipeReview recipeReview = findRecipeReview(recipeId, id);
        if (!Objects.equals(recipeReview.getUser(), user.getNickname()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        return recipeReview;
    }

    private RecipeReview findRecipeReview(Long recipeId, Long id){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(()-> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        return recipeReviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeReviewException(RecipeReviewErrorCode.NOT_FOUND_RECIPE_REVIEW));
    }
}
