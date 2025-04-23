package com.tablelog.tablelogback.domain.recipe.entity;


import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE")
@Entity
public class Recipe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String intro;

    @Column
    private String folderName;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private int state;

    @Column(nullable = false)
    private RecipeCategory recipeCategory;

    @Column(nullable = false)
    private Byte star;

    @Column
    private Integer price;

    @Column(nullable = false)
    private String memo;

    @Column(nullable = false)
    private String cookingTime;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column(nullable = false)
    private Integer point;

    @Builder
    public Recipe(final String title, final String intro, final String folderName,
                  final String imageUrl, final int state, final RecipeCategory recipeCategory,
                  final Byte star, final Integer price, final String memo, final String cookingTime,
                  final Boolean isPaid, final Integer point){
        this.title = title;
        this.intro = intro;
        this.folderName = folderName;
        this.imageUrl = imageUrl;
        this.state = state;
        this.recipeCategory = recipeCategory;
        this.star = star;
        this.price = price;
        this.memo = memo;
        this.cookingTime = cookingTime;
        this.isPaid = isPaid;
        this.point = 0;
    }
}
