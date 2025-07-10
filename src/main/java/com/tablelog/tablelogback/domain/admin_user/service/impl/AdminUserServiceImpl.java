package com.tablelog.tablelogback.domain.admin_user.service.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserRejectReasonServiceRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.domain.admin_user.entity.AdminUser;
import com.tablelog.tablelogback.domain.admin_user.exception.AdminUserErrorCode;
import com.tablelog.tablelogback.domain.admin_user.exception.NotFoundAdminUserException;
import com.tablelog.tablelogback.domain.admin_user.mapper.entity.AdminUserEntityMapper;
import com.tablelog.tablelogback.domain.admin_user.repository.AdminUserRepository;
import com.tablelog.tablelogback.domain.admin_user.service.AdminUserService;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.follow.repository.FollowRepository;
import com.tablelog.tablelogback.domain.recipe.repository.RecipeRepository;
import com.tablelog.tablelogback.domain.recipe_like.repository.RecipeLikeRepository;
import com.tablelog.tablelogback.domain.recipe_memo.repository.RecipeMemoRepository;
import com.tablelog.tablelogback.domain.recipe_review.repository.RecipeReviewRepository;
import com.tablelog.tablelogback.domain.recipe_save.repository.RecipeSaveRepository;
import com.tablelog.tablelogback.domain.shopping_list.repository.ShoppingListRepository;
import com.tablelog.tablelogback.domain.user.entity.OAuthAccount;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundOAuthAccountException;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.OAuthAccountRepository;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.GoogleService;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import com.tablelog.tablelogback.domain.user_license.repository.UserLicenseRepository;
import com.tablelog.tablelogback.global.enums.*;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.s3.S3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final FollowRepository followRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeMemoRepository recipeMemoRepository;
    private final RecipeSaveRepository recipeSaveRepository;
    private final RecipeReviewRepository recipeReviewRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final S3Provider s3Provider;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final RecipeRepository recipeRepository;
    private final UserLicenseRepository userLicenseRepository;
    private final AdminUserEntityMapper adminUserEntityMapper;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";

    @Scheduled(cron = "0 0 0 * * *") // 매일 00시
    public void processPendingWithdrawals(){
        // 별도 요청 테이블 -> DB 부하
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        List<User> users = userRepository.findByIsDeletedAndModifiedAtBefore(true, cutoff);
        for(User user : users){
            if(!adminUserRepository.existsByUserId(user.getId())){
                AdminUser adminUser = AdminUser.builder()
                        .userId(user.getId())
                        .status(ApplyStatus.APPLIED)
                        .requestType(AdminRequestType.WITHDRAWAL)
                .build();
                adminUserRepository.save(adminUser);
                System.out.println(adminUser.getUserId());
            }
        }
    }

    @Transactional
    public void approveDeleteUser(Long id) throws JacksonException {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundAdminUserException(AdminUserErrorCode.NOT_FOUND_ADMIN_USER));
        User user = userRepository.findById(adminUser.getUserId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Long userId = user.getId();

        // 활동 내역 삭제
        boardRepository.deleteAllByUser(user.getNickname());
        boardCommentRepository.deleteAllByUser(user.getNickname());
        boardLikeRepository.deleteAllByUser(userId);
        followRepository.deleteAllByFollowingId(userId);
        followRepository.deleteAllByFollowerId(userId);
        recipeLikeRepository.deleteAllByUser(userId);
        recipeMemoRepository.deleteAllByUserId(userId);
        recipeSaveRepository.deleteAllByUser(userId);
        recipeReviewRepository.deleteAllByUser(user.getNickname());
        shoppingListRepository.countAllByUserId(userId);

        List<OAuthAccount> accounts = oAuthAccountRepository.findAllByUserId(userId)
                .orElseThrow(() -> new NotFoundOAuthAccountException(UserErrorCode.NOT_FOUND_SOCIAL_ACCOUNT));
        for(OAuthAccount oAuthAccount : accounts){
            if(oAuthAccount.getProvider() == UserProvider.kakao){
                // admin 키로 삭제 예정
//                kakaoService.unlinkKakao(kakaoAccessToken);
            } else if(oAuthAccount.getProvider() == UserProvider.google){
                String googleAccessToken = googleService.reissueToken(user).get("access_token").asText();
                googleService.unlinkGoogle(googleAccessToken);
            }
        }
        oAuthAccountRepository.deleteAllByUserId(userId);
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));

        if (user.getProfileImgUrl() == null){
            userRepository.deleteById(user.getId());
        } else {
            String image_name = user.getProfileImgUrl().replace(url, "");
            image_name = image_name.substring(image_name.lastIndexOf("/"));
            userRepository.deleteById(user.getId());
            s3Provider.delete(user.getFolderName() + image_name);
        }

        adminUser.updateStatus(ApplyStatus.APPROVED);
        adminUserRepository.save(adminUser);
    }

    @Override
    public AdminUserSliceReadResponseDto getAllAdminUser(ApplyStatus status, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<AdminUser> slice;
        if(status == null){
            slice = adminUserRepository.findAll(pageRequest);
        } else {
            slice = adminUserRepository.findAllByStatus(status, pageRequest);
        }
        List<AdminUserReadResponseDto> responseDtos = adminUserEntityMapper.toAdminUserResponseDto(slice.getContent());
        return new AdminUserSliceReadResponseDto(responseDtos, slice.hasNext());
    }

    @Override
    public void reviewExpertVerification(Long id){
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundAdminUserException(AdminUserErrorCode.NOT_FOUND_ADMIN_USER));
        User user = userRepository.findById(adminUser.getUserId())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Long recipeCount = recipeRepository.countByUserId(user.getId());
        user.updateRecipeCount(recipeCount);
        boolean hasBusinessLicense = userLicenseRepository
                .existsByUserIdAndLicenseType(user.getId(), LicenseType.BUSINESS_REGISTRATION);
        boolean hasPatent = userLicenseRepository.existsByUserIdAndLicenseType(user.getId(), LicenseType.PATENT);
        if (recipeCount >= 50 || hasBusinessLicense || hasPatent) {
            user.changeRole(UserRole.EXPERT);
            adminUser.updateStatus(ApplyStatus.APPROVED);
            userRepository.save(user);
            adminUserRepository.save(adminUser);
        } else {
            adminUser.updateStatus(ApplyStatus.REJECTED);
            adminUser.updateRejectReason("조건이 충족되지 않습니다");
            adminUserRepository.save(adminUser);
        }
    }

    @Override
    public void rejectExpertVerification(
            Long id, AdminUserRejectReasonServiceRequestDto serviceRequestDto
    ){
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundAdminUserException(AdminUserErrorCode.NOT_FOUND_ADMIN_USER));
        adminUser.updateStatus(ApplyStatus.REJECTED);
        adminUser.updateRejectReason(serviceRequestDto.rejectReason());
        adminUserRepository.save(adminUser);
    }
}
