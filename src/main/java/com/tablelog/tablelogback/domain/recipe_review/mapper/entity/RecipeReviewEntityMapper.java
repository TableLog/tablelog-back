package com.tablelog.tablelogback.domain.recipe_review.mapper.entity;

import com.tablelog.tablelogback.domain.recipe_review.dto.service.RecipeReviewCreateServiceRequestDto;
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

    RecipeReviewReadResponseDto toRecipeReviewReadResponseDto(
            RecipeReview recipeReview, Boolean isReviewer, String profileImgUrl);

    default RecipeReviewReadResponseDto toDtoWithReply(
            RecipeReview recipeReview,
            boolean isReviewer,
            String profileImgUrl,
            RecipeReviewReadResponseDto reply
    ) {
        RecipeReviewReadResponseDto base = toRecipeReviewReadResponseDto(recipeReview, isReviewer, profileImgUrl);
        return new RecipeReviewReadResponseDto(
                base.id(),
                base.content(),
                base.star(),
                base.recipeId(),
                base.user(),
                base.modifiedAt(),
                base.prrId(),
                base.isReviewer(),
                base.profileImgUrl(),
                reply
        );
    }

    @Mapping(source = "modifiedAt", target = "modifiedAt")
    List<RecipeReviewReadResponseDto> toRecipeReviewReadAllResponseDtoLists(List<RecipeReview> recipeReviewList);
}
