package com.tablelog.tablelogback.domain.recipe_payment.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.PaymentMethod;
import com.tablelog.tablelogback.global.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_RECIPE_PAYMENT")
public class RecipePayment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Builder
    public RecipePayment(final Long userId, final Long recipeId, final PaymentMethod paymentMethod,
                         final PaymentStatus paymentStatus){
        this.userId = userId;
        this.recipeId = recipeId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus){
        this.paymentStatus = paymentStatus;
    }
}
