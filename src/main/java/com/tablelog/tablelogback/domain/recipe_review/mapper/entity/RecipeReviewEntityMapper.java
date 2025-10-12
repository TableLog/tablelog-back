package com.tablelog.tablelogback.domain.recipe_review.mapper.entity;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseByUserDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReadResponseDto;
import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewReplyCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipeReviewEntityMapper {
    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "recipeId", target = "recipeId")
    @Mapping(source = "prrId", target = "prrId")
    RecipeReview toRecipeReview(RecipeReviewCreateServiceRequestDto serviceRequestDto,
                                Long recipeId, User user, Long prrId);

    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "recipeId", target = "recipeId")
    RecipeReview toRecipeReply(RecipeReviewReplyCreateServiceRequestDto serviceRequestDto, Long recipeId, User user);

    default RecipeReviewReadResponseDto toRecipeReviewReadResponseDto(
            RecipeReview review,
            boolean isReviewer,
            String profileImgUrl
    ) {
        return new RecipeReviewReadResponseDto(
                review.getId(),
                review.getContent(),
                review.getStar(),
                review.getRecipeId(),
                review.getUser(),
                review.getModifiedAt(),
                review.getPrrId(),
                isReviewer,
                profileImgUrl,
                null
        );
    }

    default RecipeReviewReadResponseDto toDtoWithReply(
            RecipeReview review,
            boolean isReviewer,
            String profileImgUrl,
            RecipeReviewReadResponseDto reply
    ) {
        return new RecipeReviewReadResponseDto(
                review.getId(),
                review.getContent(),
                review.getStar(),
                review.getRecipeId(),
                review.getUser(),
                review.getModifiedAt(),
                review.getPrrId(),
                isReviewer,
                profileImgUrl,
                reply
        );
    }

    default RecipeReviewReadResponseByUserDto toRecipeReviewReadResponseByUserDto(
            RecipeReview review,
            boolean isReviewer,
            String profileImgUrl,
            String title,
            String imageUrl
    ) {
        return new RecipeReviewReadResponseByUserDto(
                review.getId(),
                review.getContent(),
                review.getStar(),
                review.getRecipeId(),
                review.getUser(),
                review.getModifiedAt(),
                review.getPrrId(),
                isReviewer,
                profileImgUrl,
                title,
                imageUrl,
                null
        );
    }

    default RecipeReviewReadResponseByUserDto toDtoWithReplyByUser(
            RecipeReview review,
            boolean isReviewer,
            String profileImgUrl,
            String title,
            String imageUrl,
            RecipeReviewReadResponseByUserDto reply
    ) {
        return new RecipeReviewReadResponseByUserDto(
                review.getId(),
                review.getContent(),
                review.getStar(),
                review.getRecipeId(),
                review.getUser(),
                review.getModifiedAt(),
                review.getPrrId(),
                isReviewer,
                profileImgUrl,
                title,
                imageUrl,
                reply
        );
    }

    @Mapping(source = "modifiedAt", target = "modifiedAt")
    List<RecipeReviewReadResponseDto> toRecipeReviewReadAllResponseDtoLists(List<RecipeReview> recipeReviewList);
}
