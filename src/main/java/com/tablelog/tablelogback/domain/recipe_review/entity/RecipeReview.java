package com.tablelog.tablelogback.domain.recipe_review.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_REVIEW")
@Entity
public class RecipeReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300)
    private String content;

    @DecimalMin(value = "0.0", inclusive = true, message = "별점은 0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", inclusive = true, message = "별점은 5 이하이어야 합니다.")
    private Float star;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private String user;

    @Column
    private Long prrId; // parentRecipeReviewId

    @Builder
    public RecipeReview(final String content, final Float star, final Long recipeId,
                        final String user, final Long prrId){
        this.content = content;
        this.star = star;
        this.recipeId = recipeId;
        this.user = user;
        this.prrId = prrId;
    }

    public void updateRecipeReview(final String content, final Float star, final Long recipeId,
                                   final String user, final Long prrId){
        this.content = content;
        this.star = star;
        this.recipeId = recipeId;
        this.user = user;
        this.prrId = prrId;
    }
}
