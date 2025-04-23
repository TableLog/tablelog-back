package com.tablelog.tablelogback.domain.food.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_FOOD")
public class Food extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String foodName;

    @Column(nullable = false)
    private FoodUnit foodUnit;

    @Column(nullable = false)
    private Integer cal;

    @Builder
    public Food(final String foodName, final FoodUnit foodUnit, final Integer cal){
        this.foodName = foodName;
        this.foodUnit = foodUnit;
        this.cal = cal;
    }

    public void updateFood(String foodName, FoodUnit foodUnit, Integer cal){
        this.foodName = foodName;
        this.foodUnit = foodUnit;
        this.cal = cal;
    }
}
