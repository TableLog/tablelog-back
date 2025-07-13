package com.tablelog.tablelogback.domain.recipe_review.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.*;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // 작성자 댓글 생성 불가
        if(Objects.equals(user.getId(), recipe.getUserId())){
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReview(serviceRequestDto, recipeId, user, 0L);
        recipeReviewRepository.save(recipeReview);
        recipe.updateReviewCount(recipe.getReviewCount() + 1);
        recipe.addStar(serviceRequestDto.star());
        user.addPointBalance(100);
    }

    @Transactional
    public void createRecipeReply(RecipeReviewReplyCreateServiceRequestDto serviceRequestDto, Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        // 대댓글은 작성자만 한 개만 가능
        if(serviceRequestDto.prrId() == 0L){
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        if(recipeReviewRepository.existsByPrrId(serviceRequestDto.prrId()) || !isRecipeAuthorOrAdmin(recipe, user)){
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReply(serviceRequestDto, recipeId, user);
        recipeReviewRepository.save(recipeReview);
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
        User reviewUser = userRepository.findByNickname(recipeReview.getUser())
                .orElse(null);
        String profileImgUrl = Optional.ofNullable(reviewUser)
                .map(User::getProfileImgUrl)
                .orElse(null);
        return recipeReviewEntityMapper.toRecipeReviewReadResponseDto(recipeReview, isReviewer, profileImgUrl);
    }

    @Override
    public RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(
            Long recipeId, int pageNumber, UserDetailsImpl userDetails
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));

        // 댓글만 조회
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByRecipeIdAndPrrId(recipe.getId(), 0L, pageRequest);

        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, false);
        boolean isWriter = userDetails != null && userDetails.user().getId().equals(recipe.getUserId());
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext(), isWriter);
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
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext(), null);
    }

    @Override
    public RecipeReviewSliceResponseDto getAllMyRecipeReviews(UserDetailsImpl userDetails, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByUser(userDetails.user().getNickname(), pageRequest);
        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, true);
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext(), null);
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
            Slice<RecipeReview> slice, UserDetailsImpl userDetails, boolean isMyReview
    ){
        List<RecipeReview> comments = slice.getContent();

        // 댓글 id 조회
        List<Long> commentIds = comments.stream()
                .map(RecipeReview::getId)
                .collect(Collectors.toList());

        // 답글 조회
        List<RecipeReview> replies = recipeReviewRepository.findAllByPrrIdIn(commentIds);
        Map<Long, RecipeReview> replyMap = replies.stream()
                .collect(Collectors.toMap(RecipeReview::getPrrId, Function.identity()));

        // 유저 집합
        Set<String> allNicknames = Stream.concat(
                comments.stream().map(RecipeReview::getUser),
                replies.stream().map(RecipeReview::getUser)
        ).collect(Collectors.toSet());

        // 유저 프로필 이미지
        Map<String, String> profileImgMap = userRepository.findAllByNicknameIn(allNicknames).stream()
                .filter(user -> user.getNickname() != null) // null 방지
                .collect(Collectors.toMap(
                        User::getNickname,
                        user -> user.getProfileImgUrl() != null ? user.getProfileImgUrl() : ""
                ));

        return comments.stream()
                .map(comment -> {
                    boolean isReviewer = isMyReview
                            || (userDetails != null && userDetails.user().getNickname().equals(comment.getUser()));

                    String profileImgUrl = profileImgMap.get(comment.getUser());

                    // 답글 매핑
                    RecipeReview reply = replyMap.get(comment.getId());
                    RecipeReviewReadResponseDto replyDto = null;
                    if (reply != null) {
                        boolean isReplyReviewer = userDetails != null
                                && userDetails.user().getNickname().equals(reply.getUser());
                        String replyProfileImgUrl = profileImgMap.get(reply.getUser());
                        replyDto = recipeReviewEntityMapper
                                .toRecipeReviewReadResponseDto(reply, isReplyReviewer, replyProfileImgUrl);
                    }

                    return recipeReviewEntityMapper.toDtoWithReply(comment, isReviewer, profileImgUrl, replyDto);
                })
                .collect(Collectors.toList());
    }
}
