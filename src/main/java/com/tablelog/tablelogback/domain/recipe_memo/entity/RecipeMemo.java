package com.tablelog.tablelogback.domain.recipe_memo.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_MEMO")
public class RecipeMemo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 500)
    private String memo;

    @Builder
    public RecipeMemo(final Long recipeId, final Long userId, final String memo){
        this.recipeId = recipeId;
        this.userId = userId;
        this.memo = memo;
    }

    public void updateRecipeMemo(String memo){
        this.memo = memo;
    }
}
