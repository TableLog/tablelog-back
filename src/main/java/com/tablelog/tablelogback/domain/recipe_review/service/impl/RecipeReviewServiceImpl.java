package com.tablelog.tablelogback.domain.recipe_review.service.impl;

import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.domain.point_transaction.repository.PointTransactionRepository;
import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.*;
import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import com.tablelog.tablelogback.domain.recipe_review.exception.DuplicateRecipeReviewUserException;
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
import com.tablelog.tablelogback.global.enums.PointReason;
import com.tablelog.tablelogback.global.enums.PointType;
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
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public void createRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto, Long recipeId, User user){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        // 작성자 댓글 생성 불가
        if(Objects.equals(user.getId(), recipe.getUserId())){
            throw new ForbiddenAccessRecipeReviewException(RecipeReviewErrorCode.FORBIDDEN_ACCESS_RECIPE_REVIEW);
        }
        // 작성자 댓글 중복 작성 불가
        if(recipeReviewRepository.existsByRecipeIdAndUserAndPrrId(recipeId, user.getNickname(), 0L)){
            throw new DuplicateRecipeReviewUserException(RecipeReviewErrorCode.DUPLICATE_RECIPE_REVIEW_USER);
        }
        RecipeReview recipeReview = recipeReviewEntityMapper.toRecipeReview(serviceRequestDto, recipeId, user, 0L);
        recipeReviewRepository.save(recipeReview);
        recipe.updateReviewCount(recipe.getReviewCount() + 1);
        recipe.addStar(serviceRequestDto.star());
        user.addPointBalance(100);
        PointTransaction pointTransaction = PointTransaction.builder()
                .userId(user.getId())
                .amount(100)
                .pointReason(PointReason.레시피댓글등록)
                .pointType(PointType.EARN)
                .build();
        pointTransactionRepository.save(pointTransaction);
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
    public RecipeReviewReadResponseDto readRecipeReview(
            Long recipeId, Long id, Boolean includeReplies, UserDetailsImpl userDetails
    ) {
        recipeRepository.findById(recipeId)
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

        // 답글 포함 조회
        if (Boolean.TRUE.equals(includeReplies)) {
            RecipeReview reply = recipeReviewRepository.findAllByPrrId(recipeReview.getId());
            if(reply != null) {
                User replyUser = userRepository.findByNickname(reply.getUser())
                        .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
                String replyProfileImgUrl = replyUser.getProfileImgUrl();
                boolean isReplyReviewer = false;
                if (userDetails != null) {
                    isReplyReviewer = userDetails.user().getNickname().equals(reply.getUser());
                }
                RecipeReviewReadResponseDto replyDto = recipeReviewEntityMapper
                        .toRecipeReviewReadResponseDto(reply, isReplyReviewer, replyProfileImgUrl);
                return recipeReviewEntityMapper.toDtoWithReply(recipeReview, isReviewer, profileImgUrl, replyDto);
            }
        }
        // 답글 없이 단건 조회
        return recipeReviewEntityMapper.toDtoWithReply(recipeReview, isReviewer, profileImgUrl, null);
    }

    @Override
    public RecipeReviewSliceResponseDto readAllRecipeReviewsByRecipe(
            Long recipeId, int pageNum, UserDetailsImpl userDetails
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));

        // 댓글만 조회
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByRecipeIdAndPrrId(recipe.getId(), 0L, pageRequest);

        List<RecipeReviewReadResponseDto> recipeReviews = mappingRecipeReviews(slice, userDetails, false);
        boolean isWriter = userDetails != null && userDetails.user().getId().equals(recipe.getUserId());
        return new RecipeReviewSliceResponseDto(recipeReviews, slice.hasNext(), isWriter);
    }

    @Override
    public RecipeReviewSliceResponseByUserDto readAllRecipeReviewsByUser(
            Long userId, int pageNum, UserDetailsImpl userDetails
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByUser(user.getNickname(), pageRequest);
        boolean isMyReview = false;
        if(userDetails != null && userDetails.user().getId().equals(userId)){
            isMyReview = true;
        }
        List<RecipeReviewReadResponseByUserDto> recipeReviews = mappingRecipeReviewsByUser(slice, userDetails, isMyReview);
        return new RecipeReviewSliceResponseByUserDto(recipeReviews, slice.hasNext(), null);
    }

    @Override
    public RecipeReviewSliceResponseByUserDto readAllMyRecipeReviews(UserDetailsImpl userDetails, int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecipeReview> slice = recipeReviewRepository.findAllByUser(userDetails.user().getNickname(), pageRequest);
        List<RecipeReviewReadResponseByUserDto> recipeReviews = mappingRecipeReviewsByUser(slice, userDetails, true);
        return new RecipeReviewSliceResponseByUserDto(recipeReviews, slice.hasNext(), null);
    }

    @Transactional
    public void updateRecipeReview(RecipeReviewUpdateServiceRequestDto requestDto, Long recipeId, Long id, User user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        RecipeReview recipeReview = validateRecipeReview(id, user);

        if(recipeReview.getPrrId() == 0){
            Float oldStar = recipeReview.getStar();
            Float newStar = requestDto.star();
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

        // 레시피 id 조회
        List<Long> recipeIds = comments.stream()
                .map(RecipeReview::getRecipeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 레시피 조회 및 매핑
        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);
        Map<Long, Recipe> recipeMap = recipes.stream()
                .collect(Collectors.toMap(Recipe::getId, Function.identity()));

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
                        boolean isReplyReviewer = userDetails != null &&
                                userDetails.user().getNickname().equals(reply.getUser());
                        String replyProfileImgUrl = profileImgMap.get(reply.getUser());

                        replyDto = recipeReviewEntityMapper.toRecipeReviewReadResponseDto(
                                reply, isReplyReviewer, replyProfileImgUrl
                        );
                    }
                    return recipeReviewEntityMapper.toDtoWithReply(
                            comment, isReviewer, profileImgUrl, replyDto);
                })
                .collect(Collectors.toList());
    }


    private List<RecipeReviewReadResponseByUserDto> mappingRecipeReviewsByUser(
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

        // 레시피 id 조회
        List<Long> recipeIds = comments.stream()
                .map(RecipeReview::getRecipeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 레시피 조회 및 매핑
        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);
        Map<Long, Recipe> recipeMap = recipes.stream()
                .collect(Collectors.toMap(Recipe::getId, Function.identity()));

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

                    // 댓글용 레시피 정보
                    Recipe commentRecipe = recipeMap.get(comment.getRecipeId());
                    String commentImageUrl = commentRecipe != null ? commentRecipe.getImageUrl() : null;
                    String commentTitle = commentRecipe != null ? commentRecipe.getTitle() : null;

                    // 답글 매핑
                    RecipeReview reply = replyMap.get(comment.getId());
                    RecipeReviewReadResponseByUserDto replyDto = null;
                    if (reply != null) {
                        boolean isReplyReviewer = userDetails != null &&
                                userDetails.user().getNickname().equals(reply.getUser());
                        String replyProfileImgUrl = profileImgMap.get(reply.getUser());

                        Recipe replyRecipe = recipeMap.get(reply.getRecipeId());
                        String replyImageUrl = replyRecipe != null ? replyRecipe.getImageUrl() : null;
                        String replyTitle = replyRecipe != null ? replyRecipe.getTitle() : null;

                        replyDto = recipeReviewEntityMapper.toRecipeReviewReadResponseByUserDto(
                                reply, isReplyReviewer, replyProfileImgUrl, replyTitle, replyImageUrl
                        );
                    }
                    return recipeReviewEntityMapper.toDtoWithReplyByUser(
                            comment, isReviewer, profileImgUrl, commentTitle, commentImageUrl, replyDto);
                })
                .collect(Collectors.toList());
    }
}
