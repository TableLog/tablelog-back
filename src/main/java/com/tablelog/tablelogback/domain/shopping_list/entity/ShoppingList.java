package com.tablelog.tablelogback.domain.shopping_list.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.FoodUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_SHOPPING_LIST")
public class ShoppingList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private FoodUnit foodUnit;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Long foodId;

    @Column
    private Boolean isChecked = false;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public ShoppingList(final FoodUnit foodUnit, final Integer amount,
                        final Long foodId, final Long userId){
        this.foodUnit = foodUnit;
        this.amount = amount;
        this.foodId = foodId;
        this.userId = userId;
    }

    public void updateIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }
}
