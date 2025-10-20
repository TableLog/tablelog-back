package com.tablelog.tablelogback.domain.recipe_process.entity;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
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
@Entity
@Table(name = "TB_RECIPE_PROCESS", uniqueConstraints = {@UniqueConstraint(columnNames = {"recipe_id", "sequence"})})
public class RecipeProcess extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence", nullable = false)
    private short sequence;

    @Column(nullable = false)
    private String rpTitle;

    @Column(nullable = false)
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_process_image_urls")
    @Column(name = "image_url")
    private List<String> recipeProcessImageUrls = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Builder
    public RecipeProcess(
            final short sequence, final String rpTitle,
            final String description, final Recipe recipe,
            List<String> recipeProcessImageUrls
    ){
        this.sequence = sequence;
        this.rpTitle = rpTitle;
        this.description = description;
        this.recipe = recipe;
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
