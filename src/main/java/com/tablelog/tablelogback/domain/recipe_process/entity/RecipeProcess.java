package com.tablelog.tablelogback.domain.recipe_process.entity;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String description;

    @Column
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Builder
    public RecipeProcess(
            final short sequence, final String description, final String imgUrl, final Recipe recipe
    ) {
        this.sequence = sequence;
        this.description = description;
        this.imgUrl = imgUrl;
        this.recipe = recipe;
    }

    public void updateRecipeProcess(
            final short sequence, final String description, final String imgUrl
    ) {
        this.sequence = sequence;
        this.description = description;
        this.imgUrl = imgUrl;
    }
}
