package com.tablelog.tablelogback.domain.recipe_like.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_LIKE")
public class RecipeLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user;

    @Column(nullable = false)
    private Long recipe;

    @Builder
    public RecipeLike(final Long user, final Long recipe){
        this.user = user;
        this.recipe = recipe;
    }
}
