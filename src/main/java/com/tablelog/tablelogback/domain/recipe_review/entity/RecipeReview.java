package com.tablelog.tablelogback.domain.recipe_review.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
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

    @Column
    private String content;

    @Column
    private Byte star;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private String user;

    @Column
    private Long prrId; // parentRecipeReviewId

    @Builder
    public RecipeReview(final String content, final Byte star, final Long recipeId,
                        final String user, final Long prrId){
        this.content = content;
        this.star = star;
        this.recipeId = recipeId;
        this.user = user;
        this.prrId = prrId;
    }

    public void updateRecipeReview(final String content, final Byte star, final Long recipeId,
                                   final String user, final Long prrId){
        this.content = content;
        this.star = star;
        this.recipeId = recipeId;
        this.user = user;
        this.prrId = prrId;
    }
}
