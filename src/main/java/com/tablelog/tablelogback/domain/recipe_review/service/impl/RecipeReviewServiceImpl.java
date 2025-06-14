package com.tablelog.tablelogback.domain.recipe_review.service.impl;

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
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeReviewServiceImpl implements RecipeReviewService {
    private final RecipeReviewRepository recipeReviewRepository;
    private final RecipeReviewEntityMapper recipeReviewEntityMapper;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        // 대댓글은 작성자만 한 개만 가능
        if(serviceRequestDto.prrId() != 0){
            if(recipeReviewRepository.existsByPrrId(serviceRequestDto.prrId()) || !isRecipeAuthorOrAdmin(recipe, user)){
                throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
            }
            RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReview(serviceRequestDto, recipeId, user);
            recipeReviewRepository.save(recipeReview);
        }
        else {
            RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReview(serviceRequestDto, recipeId, user);
            recipeReviewRepository.save(recipeReview);
            recipe.updateReviewCount(recipe.getReviewCount() + 1);
            recipe.addStar(serviceRequestDto.star());
            user.addPointBalance(100);
        }
    }

    @Override
    public RecipeReviewReadResponseDto readRecipeReview(Long recipeId, Long id, UserDetailsImpl userDetails) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(()-> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        RecipeReview recipeReview = recipeReviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeReviewException(RecipeReviewErrorCode.NOT_FOUND_RECIPE_REVIEW));
        boolean isReviewer = false;
        if(userDetails != null){
            isReviewer = userDetails.user().getNickname().equals(recipeReview.getUser());
        }
        return recipeReviewEntityMapper.toRecipeReviewReadResponseDto(recipeReview, isReviewer);
    }

    @Override
    public RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(
            Long recipeId, int pageNumber, UserDetailsImpl userDetails) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByRecipeId(recipe.getId(), pageRequest);
        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, false);
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext());
    }

    @Override
    public RecipeReviewSliceResponseDto readAllRecipeReviewsByUser(Long userId, int pageNumber, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByUser(user.getNickname(), pageRequest);
        boolean isMyReview = false;
        if(userDetails != null && userDetails.user().getId().equals(userId)){
            isMyReview = true;
        }
        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, isMyReview);
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext());
    }

    @Override
    public RecipeReviewSliceResponseDto getAllMyRecipeReviews(UserDetailsImpl userDetails, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByUser(userDetails.user().getNickname(), pageRequest);
        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, true);
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext());
    }

    @Transactional
    public void updateRecipeReview(RecipeReviewUpdateServiceRequestDto requestDto, Long recipeId, Long id, User user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        RecipeReview recipeReview = validateRecipeReview(id, user);

        if(recipeReview.getPrrId() == 0){
            byte oldStar = recipeReview.getStar();
            byte newStar = requestDto.star();
            recipeReview.updateRecipeReview(requestDto.content(), newStar, recipeId,
                    user.getNickname(), requestDto.prrId());
            recipe.updateStar(oldStar, newStar);
        } else {
            recipeReview.updateRecipeReview(requestDto.content(), null, recipeId,
                    user.getNickname(), requestDto.prrId());
        }
        recipeReviewRepository.save(recipeReview);
    }

    @Transactional
    public void deleteRecipeReview(Long recipeId, Long id, User user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(()-> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        RecipeReview recipeReview = validateRecipeReview(id, user);

        if(recipeReview.getPrrId() == 0){
            if(recipeReviewRepository.existsByPrrId(id)){
                recipeReviewRepository.deleteByPrrId(id);
            }
        }
        recipeReviewRepository.delete(recipeReview);
        recipe.updateReviewCount(recipe.getReviewCount() - 1);
        recipe.deleteStar(recipeReview.getStar());
        recipeRepository.save(recipe);
    }

    private RecipeReview validateRecipeReview(Long id, User user){
        RecipeReview recipeReview = recipeReviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundRecipeReviewException(RecipeReviewErrorCode.NOT_FOUND_RECIPE_REVIEW));
        if (!Objects.equals(recipeReview.getUser(), user.getNickname()) && user.getUserRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        return recipeReview;
    }

    private Boolean isRecipeAuthorOrAdmin(Recipe recipe, User user){
        if (!Objects.equals(recipe.getUserId(), user.getId()) && user.getUserRole() != UserRole.ADMIN) {
            return false;
        }
        return true;
    }

    private List<RecipeReviewReadResponseDto> mappingRecipeReviews(
            Slice<RecipeReview> slice, UserDetailsImpl userDetails, boolean isMyReview){
        List<RecipeReviewReadResponseDto> recipeReviews = slice.getContent().stream()
            .map(recipeReview -> {
                boolean isReviewer = isMyReview
                        || (userDetails != null && userDetails.user().getNickname().equals(recipeReview.getUser()));
                return recipeReviewEntityMapper.toRecipeReviewReadResponseDto(recipeReview, isReviewer);
            })
            .collect(Collectors.toList());
        return recipeReviews;
    }
}
