package com.tablelog.tablelogback.domain.recipe_payment.service.impl;

import com.tablelog.tablelogback.domain.recipe.entity.Recipe;
import com.tablelog.tablelogback.domain.recipe.exception.InsufficientPointBalanceException;
import com.tablelog.tablelogback.domain.recipe.exception.NotFoundRecipeException;
import com.tablelog.tablelogback.domain.recipe.exception.RecipeErrorCode;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_payment.entity.RecipePayment;
import com.tablelog.tablelogback.domain.recipe_payment.exception.AlreadyExistsRecipePaymentException;
import com.tablelog.tablelogback.domain.recipe_payment.exception.CannotPaymentRecipeException;
import com.tablelog.tablelogback.domain.recipe_payment.exception.RecipePaymentErrorCode;
import com.tablelog.tablelogback.domain.recipe_payment.repository.RecipePaymentRepository;
import com.tablelog.tablelogback.domain.recipe_payment.service.RecipePaymentService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.global.enums.PaymentMethod;
import com.tablelog.tablelogback.global.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipePaymentServiceImpl implements RecipePaymentService {
    private final RecipeRepository recipeRepository;
    private final RecipePaymentRepository recipePaymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRecipePayment(Long id, User user){
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(()-> new NotFoundRecipeException(RecipeErrorCode.NOT_FOUND_RECIPE));
        // 본인이 작성한 레시피
        if(user.getId().equals(recipe.getUserId())){
            throw new CannotPaymentRecipeException(RecipePaymentErrorCode.CAN_NOT_PAYMENT_RECIPE);
        }
        // 유료가 아닌 경우
        if(!recipe.getIsPaid()){
            throw new CannotPaymentRecipeException(RecipePaymentErrorCode.CAN_NOT_PAYMENT_RECIPE);
        }
        // 이미 구매
        if(recipePaymentRepository.existsByUserIdAndRecipeId(user.getId(), id)){
            throw new AlreadyExistsRecipePaymentException(RecipePaymentErrorCode.ALREADY_EXISTS_RECIPE_PAYMENT);
        }

        if(user.getPointBalance() < recipe.getRecipePoint()){
            throw new InsufficientPointBalanceException(RecipeErrorCode.INSUFFICIENT_POINT_BALANCE);
        }

        // 구매
        RecipePayment recipePayment = RecipePayment.builder()
                .userId(user.getId())
                .recipeId(recipe.getId())
                .paymentMethod(PaymentMethod.Point)
                .paymentStatus(PaymentStatus.결제중)
                .build();
        recipePaymentRepository.save(recipePayment);

        user.updatePointBalance(user.getPointBalance() - recipe.getRecipePoint());
        User writer = userRepository.findById(recipe.getUserId())
                        .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        writer.updatePointBalance(writer.getPointBalance() + recipe.getRecipePoint());
        recipePayment.updatePaymentStatus(PaymentStatus.결제완료);
    }
}
