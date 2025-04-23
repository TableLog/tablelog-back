package com.tablelog.tablelogback.domain.recipe_process.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_PROCESS")
@Entity
public class RecipeProcess extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private short sequence;

    @Column(nullable = false)
    private String rpTitle;

    @Column(nullable = false)
    private String description;

    @Column
    private List<String> recipeProcessImageUrls = new ArrayList<>();

    @Column(nullable = false)
    private Long recipeId;

    @Builder
    public RecipeProcess(
            final short sequence, final String rpTitle,
            final String description, final Long recipeId,
            List<String> recipeProcessImageUrls
    ){
        this.sequence = sequence;
        this.rpTitle = rpTitle;
        this.description = description;
        this.recipeId = recipeId;
        this.recipeProcessImageUrls = recipeProcessImageUrls;
    }

    public void updateRecipeProcess(
            short sequence, String recipeProcessTitle, String description,
            List<String> recipeProcessImageUrls
    ){
        this.sequence = sequence;
        this.rpTitle = recipeProcessTitle;
        this.description = description;
        this.recipeProcessImageUrls = recipeProcessImageUrls;
    }
}
