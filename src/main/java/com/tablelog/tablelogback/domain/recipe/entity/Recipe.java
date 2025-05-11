package com.tablelog.tablelogback.domain.recipe.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.CookingTime;
import com.tablelog.tablelogback.global.enums.RecipeCategory;
import com.tablelog.tablelogback.global.enums.RecipePrice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE")
@Entity
public class Recipe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column
    private String intro;

    @Column
    private String folderName;

    @Column
    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_recipe_category_list", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<RecipeCategory> recipeCategoryList;

    @Column(nullable = false)
    private int totalStar;

    @Column(nullable = false)
    private int starCount;

    @Column(nullable = false)
    private Float star = 0F;

    @Column
    private RecipePrice price;

    @Column(nullable = false)
    private String memo;

    @Column(nullable = false)
    private CookingTime cookingTime;

    @Column(nullable = false)
    private Integer totalCal = 0;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @Column(nullable = false)
    private Integer recipePoint = 0;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Builder
    public Recipe(final Long userId, final String title, final String intro, final String folderName,
                  final String imageUrl, final List<RecipeCategory> recipeCategoryList,
                  final RecipePrice price, final String memo, final CookingTime cookingTime,
                  final Integer totalCal, final Boolean isPaid, final Integer recipePoint
    ){
        this.userId = userId;
        this.title = title;
        this.intro = intro;
        this.folderName = folderName;
        this.imageUrl = imageUrl;
        this.recipeCategoryList = recipeCategoryList;
        this.totalStar = 0;
        this.starCount = 0;
        this.star = 0F;
        this.price = price;
        this.memo = memo;
        this.cookingTime = cookingTime;
        this.totalCal = totalCal;
        this.isPaid = isPaid;
        this.recipePoint = recipePoint;
        this.reviewCount = 0;
    }

    public void updateRecipe(final String title, final String intro, final String folderName,
                             final String imageUrl, final List<RecipeCategory> recipeCategoryList,
                             final RecipePrice price, final String memo, final CookingTime cookingTime,
                             final Boolean isPaid, final Integer recipePoint){
        this.title = title;
        this.intro = intro;
        this.folderName = folderName;
        this.imageUrl = imageUrl;
        this.recipeCategoryList = recipeCategoryList;
        this.price = price;
        this.memo = memo;
        this.cookingTime = cookingTime;
        this.isPaid = isPaid;
        this.recipePoint = recipePoint;
    }

    public void updateTotalCal(Integer totalCal){
        this.totalCal = totalCal;
    }

    public void updateIsPaid(Boolean isPaid){
        this.isPaid = isPaid;
    }

    public void updateRecipePoint(Integer recipePoint){
        this.recipePoint = recipePoint;
    }

    public void addStar(int star) {
        this.totalStar += star;
        this.starCount++;
        this.star = (float) this.totalStar / this.starCount;
    }

    public void updateStar(byte oldStar, byte newStar) {
        this.totalStar = this.totalStar - oldStar + newStar;
        this.star = (float) this.totalStar / this.starCount;
    }

    public void deleteStar(byte star) {
        this.totalStar -= star;
        this.starCount--;
        if (this.starCount > 0) {
            this.star = (float) this.totalStar / this.starCount;
        } else {
            this.star = 0F;
        }
    }

    public void updateReviewCount(Integer reviewCount){
        this.reviewCount = reviewCount;
    }
}
