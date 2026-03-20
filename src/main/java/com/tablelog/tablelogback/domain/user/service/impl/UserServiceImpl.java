package com.tablelog.tablelogback.domain.user.service.impl;

import com.tablelog.tablelogback.domain.admin_user.entity.AdminUser;
import com.tablelog.tablelogback.domain.admin_user.repository.AdminUserRepository;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.follow.dto.FollowUserDto;
import com.tablelog.tablelogback.domain.follow.dto.FollowUserListDto;
import com.tablelog.tablelogback.domain.follow.repository.FollowRepository;
import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.domain.point_transaction.repository.PointTransactionRepository;
import com.tablelog.tablelogback.domain.recipe_review.entity.RecipeReview;
import com.tablelog.tablelogback.domain.recipe_review.repository.RecipeReviewRepository;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.OAuthAccountRepository;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.OAuthAccountService;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.enums.*;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.exception.*;
import com.tablelog.tablelogback.global.s3.S3Provider;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserEntityMapper userEntityMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Provider s3Provider;
    private final OAuthAccountService oAuthAccountService;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final FollowRepository followRepository;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final AdminUserRepository adminUserRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final RecipeReviewRepository recipeReviewRepository;
    // S3 URL 대신 로컬 이미지 URL(S3Provider)을 사용
    private final String SEPARATOR = "/";
    @Value("${spring.jwt.refresh.expiration-period}")
    private Long timeToLive;

    @Override
    public void checkDuplicate(final UserSignUpServiceRequestDto serviceRequestDto){
        // 이름과 생년월일 중복 체크
        if(userRepository.existsByUserNameAndBirthday(serviceRequestDto.userName(), serviceRequestDto.birthday())){
            throw new AlreadyExistsUserException(UserErrorCode.ALREADY_EXIST_USER);
        }
        // 이메일 중복 체크
        if(userRepository.existsByEmail(serviceRequestDto.email())){
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 소셜 중복 체크 (provider, email)
        if(oAuthAccountRepository.existsByProviderAndEmail(serviceRequestDto.provider(), serviceRequestDto.email())){
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 닉네임 중복 체크
        if(userRepository.existsByNickname(serviceRequestDto.nickname())) {
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Override
    public User signUp(
            final UserSignUpServiceRequestDto serviceRequestDto,
            MultipartFile multipartFile
    ) throws IOException {
        // 중복성 검사
        checkDuplicate(serviceRequestDto);

        String fileName;
        String fileUrl = null;
        String folderName = serviceRequestDto.nickname();
        User user;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = s3Provider.originalFileName(multipartFile);
            // 로컬 저장 경로와 URL을 일치시키기 위해 S3Provider를 통해 URL 생성
            fileUrl = s3Provider.getImagePath(folderName + SEPARATOR + fileName);
            s3Provider.createFolder(folderName);
            s3Provider.saveFile(multipartFile, folderName + SEPARATOR + fileName);
        }

        if (serviceRequestDto.provider() == UserProvider.local) {
            user = userEntityMapper.toUser(serviceRequestDto, UserRole.NORMAL, fileUrl, folderName);
        } else {
            String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            fileUrl = serviceRequestDto.imgUrl();
            user = userEntityMapper.toSocialUser(serviceRequestDto, encodedPassword, UserRole.NORMAL, fileUrl, folderName);
        }
        user.addPointBalance(1000);
        userRepository.save(user);
        PointTransaction pointTransaction = PointTransaction.builder()
                .userId(user.getId())
                .amount(1000)
                .pointReason(PointReason.회원가입)
                .pointType(PointType.EARN)
                .build();
        pointTransactionRepository.save(pointTransaction);
        return user;
    }

    @Override
    public UserLoginDto login(final UserLoginServiceRequestDto userLoginServiceRequestDto) {
        User user = userRepository.findByEmail(userLoginServiceRequestDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(!passwordEncoder.matches(userLoginServiceRequestDto.password(),user.getPassword())){
            throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
        }

        // 탈퇴한 사용자는 로그인 불가
        if(user.getIsDeleted()){
            throw new NotFoundUserException(UserErrorCode.NOT_FOUND_USER);
        }

        jwtUtil.addTokenToCookie(user, httpServletResponse, "accessToken");
        String refresh = jwtUtil.addTokenToCookie(user, httpServletResponse, "refreshToken");
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.readAllOAuthAccountDtos(user.getId());

        return new UserLoginDto(user.getUserRole());
    }

    @Override
    public UserLoginResponseDto readUser(final String token){
        if (jwtUtil.isExpiredAccessToken(token)) {
            throw new ExpiredJwtAccessTokenException(JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
        }
        String email = jwtUtil.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.readAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
    }

    @Override
    public UserProfileDto readUserProfile(Long userId, UserDetailsImpl userDetails){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        Boolean isFollowed = userDetails != null
                && followRepository.existsByFollowerIdAndFollowingId(userDetails.user().getId(), userId);
        return userEntityMapper.toUserProfileDto(user, isFollowed);
    }

    @Override
    public FollowUserListDto findUsers(String keyword, int pageNum, UserDetailsImpl userDetails){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));

        Slice<User> slice;
        if(keyword != null && !keyword.isBlank()){
            // keyword 검색 시 전체 유저 검색
            slice = userRepository.findByNicknameContaining(keyword, pageRequest);
        } else {
            // 기본: 내가 팔로우한 유저들 조회
            slice = userRepository.findAll(pageRequest);
        }

        List<User> users = slice.getContent();
        List<Long> targetIds = users.stream()
                .map(User::getId)
                .toList();
        Long userId = (userDetails != null) ? userDetails.user().getId() : null;
        Set<Long> idsIsFollow;
        if(userDetails != null) {
            idsIsFollow = new HashSet<>(
                    followRepository.findAllFollowingIdsByFollowerId(userId, targetIds)
            );
        } else {
            idsIsFollow = Collections.emptySet();;
        }
        List<FollowUserDto> dtos = users.stream()
                .map(user -> new FollowUserDto(
                        user.getId(),
                        user.getNickname(),
                        user.getProfileImgUrl(),
                        idsIsFollow.contains(user.getId())
                ))
                .toList();
        return new FollowUserListDto(dtos, slice.hasNext());
    }

    @Transactional
    public void updateUser(User user,
                           UpdateUserServiceRequestDto serviceRequestDto,
                           MultipartFile multipartFile,
                           HttpServletResponse response
    ) throws IOException {
        // 소셜 미연동 시
        if(user.getProvider() == UserProvider.local){
            // 이메일
            if(!serviceRequestDto.email().equals(user.getEmail())){
                if(userRepository.existsByEmail(serviceRequestDto.email())){
                    throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
                }
                user.updateEmail(serviceRequestDto.email());
                // 이메일 변경하면 리프레시
                jwtUtil.deleteCookie("accessToken", response);
                jwtUtil.addTokenToCookie(user, response, "accessToken");
                jwtUtil.deleteCookie("refreshToken", response);
                refreshTokenRepository.deleteById(String.valueOf(user.getId()));
                String newToken = jwtUtil.addTokenToCookie(user, response, "refreshToken");
                refreshTokenRepository.save(new RefreshToken(user.getId(), newToken, timeToLive));
            }

            // 비밀번호
            if(!Objects.equals(serviceRequestDto.password(), "")){
                if (passwordEncoder.matches(serviceRequestDto.password(), user.getPassword())) {
                    throw new NotMatchPasswordException(UserErrorCode.MATCH_CURRENT_PASSWORD);
                }
                user.updatePassword(passwordEncoder.encode(serviceRequestDto.password()));
            }
        }

        // 닉네임
        if(!serviceRequestDto.nickname().equals(user.getNickname())){
            if(userRepository.existsByNickname(serviceRequestDto.nickname())){
                throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
            }
            String oldNickname = user.getNickname();
            String newNickname = serviceRequestDto.nickname();
            user.updateNickname(newNickname);

            // 보드
            List<Board> boards = boardRepository.findAllByUser(oldNickname);
            for(Board board : boards){
                board.updateUser(newNickname);
            }
            boardRepository.saveAll(boards);

            // 보드 댓글
            List<BoardComment> comments = boardCommentRepository.findAllByUser(oldNickname);
            for(BoardComment comment : comments){
                comment.updateUser(newNickname);
            }
            boardRepository.saveAll(boards);

            // 레시피 리뷰
            List<RecipeReview> recipeReviews = recipeReviewRepository.findAllByUser(oldNickname);
            for(RecipeReview recipeReview : recipeReviews){
                recipeReview.updateUser(newNickname);
            }
            recipeReviewRepository.saveAll(recipeReviews);
        }

        // 프로필 이미지
        String imageName;
        if (multipartFile != null || !multipartFile.isEmpty()) {
            // 등록 또는 다른 사진으로 변경
            imageName = s3Provider.updateImage(user.getProfileImgUrl(), user.getFolderName(), multipartFile);
            user.updateProfileImgUrl(imageName);

        } else if (serviceRequestDto.profileImgUrl() == null || serviceRequestDto.profileImgUrl().isEmpty()) {
            // 삭제: 파일도 없고, 요청의 profileImgUrl도 없는 경우
            if (user.getProfileImgUrl() != null) {
                s3Provider.delete(user.getProfileImgUrl());
                user.updateProfileImgUrl(null);
            }
        }

        // 마케팅 동의
        if(!Objects.equals(serviceRequestDto.marketingOptIn(), user.getMarketingOptIn())){
            user.updateMarketingOptIn(serviceRequestDto.marketingOptIn());
        }

        userRepository.save(user);
    }

    @Override
    public void logout(final String accessToken, String refreshTokenCookie, final HttpServletResponse response){
        User user = null;

        try{
            jwtUtil.isExpiredAccessToken(accessToken);
        } catch(Exception e){
            String refresh = refreshTokenCookie;
            if (refreshTokenCookie.startsWith("refreshToken=")) {
                refresh = refreshTokenCookie.substring("refreshToken=".length());
            }
            Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(refresh);
            if (optional.isEmpty()) {
                jwtUtil.deleteCookie("accessToken", response);
                jwtUtil.deleteCookie("refreshToken", response);
                throw new ExpiredJwtRefreshTokenException(JwtErrorCode.EXPIRED_JWT_REFRESH_TOKEN);
            }
            RefreshToken refreshToken = optional.get();
            if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
                jwtUtil.deleteCookie("accessToken", response);
                jwtUtil.deleteCookie("refreshToken", response);
                throw new FailedJwtTokenException(JwtErrorCode.FAILED_JWT_TOKEN);
            }
            user = userRepository.findById(refreshToken.getId())
                    .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        }

        if(user == null) {
            String email = jwtUtil.getUserInfoFromToken(accessToken).getSubject();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        }

        jwtUtil.deleteCookie("accessToken", response);
        jwtUtil.deleteCookie("refreshToken", response);
        if(user.getProvider() == UserProvider.google){
            jwtUtil.deleteCookie("Google-Access-Token", response);
            jwtUtil.deleteCookie("Google-Refresh-Token", response);
        }
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
    }

    @Transactional
    public void deleteUser(final User user,
                           final HttpServletResponse response
    ) {
        userRepository.findById(user.getId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        user.changeRole(UserRole.WITHDRAW);
        user.updateDeletedAt(LocalDateTime.now());
        user.updateIsDeleted(true);
        userRepository.save(user);
        // 로그아웃 처리
        jwtUtil.deleteCookie("accessToken", response);
        jwtUtil.deleteCookie("refreshToken", response);
        if(user.getProvider() == UserProvider.google){
            jwtUtil.deleteCookie("Google-Access-Token", response);
            jwtUtil.deleteCookie("Google-Refresh-Token", response);
        }
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
    }

    @Override
    public UserLoginResponseDto refreshAccessToken(final String refreshTokenCookie,
                                                   final String socialRefreshToken,
                                                   final HttpServletResponse response) {
        String refresh = refreshTokenCookie;
        if (refreshTokenCookie.startsWith("refreshToken=")) {
            refresh = refreshTokenCookie.substring("refreshToken=".length());
        }
        Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(refresh);
        if (optional.isEmpty()) {
            jwtUtil.deleteCookie("accessToken", response);
            jwtUtil.deleteCookie("refreshToken", response);
            throw new ExpiredJwtRefreshTokenException(JwtErrorCode.EXPIRED_JWT_REFRESH_TOKEN);
        }
        RefreshToken refreshToken = optional.get();
        if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
            jwtUtil.deleteCookie("accessToken", response);
            jwtUtil.deleteCookie("refreshToken", response);
            throw new FailedJwtTokenException(JwtErrorCode.FAILED_JWT_TOKEN);
        }
        User user = userRepository.findById(refreshToken.getId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        // accessToken과 refreshToken 둘 다 refresh
        jwtUtil.deleteCookie("accessToken", response);
        jwtUtil.addTokenToCookie(user, response, "accessToken");
        jwtUtil.deleteCookie("refreshToken", response);
        String newToken = jwtUtil.addTokenToCookie(user, response, "refreshToken");
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
        refreshTokenRepository.save(new RefreshToken(user.getId(), newToken, timeToLive));
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.readAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
    }

    @Override
    public void isNotDupUserEmail(isNotDupUserEmailServiceRequestDto serviceRequestDto) {
        if (userRepository.existsByEmail(serviceRequestDto.email())) {
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
    }

    @Override
    public void isNotDupUserNick(isNotDupUserNickServiceRequestDto serviceRequestDto){
        if(userRepository.existsByNickname(serviceRequestDto.nickname())) {
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Transactional
    public void updatePassword(UpdatePasswordServiceRequestDto serviceRequestDto){
        User user = userRepository.findByEmail(serviceRequestDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(!Objects.equals(serviceRequestDto.newPassword(), "")){
            user.updatePassword(passwordEncoder.encode(serviceRequestDto.newPassword()));
        }
    }

    @Override
    public FindEmailResponseDto findEmail(findEmailServiceRequestDto serviceRequestDto){
        User user = userRepository.findByUserNameAndBirthday(serviceRequestDto.userName(), serviceRequestDto.birthday())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        return userEntityMapper.toFindEmailResponseDto(user);
    }

    @Override
    public void requestExpertVerification(User user){
        AdminUser adminUser = AdminUser.builder()
                .userId(user.getId())
                .status(ApplyStatus.APPLIED)
                .requestType(AdminRequestType.EXPERT_VERIFY)
                .build();
        adminUserRepository.save(adminUser);
    }

    @Override
    public UserAllStatisticTypeDto readUserStatistics(){
        Long totalCount = userRepository.count();
        LocalDateTime startDate = LocalDate.now().minusDays(6).atStartOfDay();

        List<Object[]> results = userRepository.findDailySignUpCount(startDate);

        List<UserStatisticDto> dailyCounts = results.stream()
                .map(row -> new UserStatisticDto(
                        ((java.sql.Date) row[0]).toLocalDate().toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();

        UserAllStatisticDto userAllStatisticDto = new UserAllStatisticDto(totalCount, dailyCounts);
        return new UserAllStatisticTypeDto(userAllStatisticDto);
    }

    @Override
    public UserProfileByAdminSliceDto readAllUserProfileByAdmin(String keyword, int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 5, Sort.by(Sort.Direction.DESC, "id"));

        Slice<User> slice;
        if(keyword != null && !keyword.isBlank()){
            // keyword 검색 시 유저 검색
            slice = userRepository.searchUsersByKeyword(keyword, pageRequest);
        } else {
            // 기본: 전체 유저 조회
            slice = userRepository.findAll(pageRequest);
        }
        List<User> users = slice.getContent();
        List<UserProfileByAdminDto> dtos = users.stream()
                .map(user -> new UserProfileByAdminDto(
                        user.getId(),
                        user.getUserName(),
                        user.getUserRole(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getCreatedAt(),
                        user.getProvider(),
                        oAuthAccountService.readAllOAuthAccountDtos(user.getId())
                ))
                .toList();
        return new UserProfileByAdminSliceDto(dtos, slice.hasNext());
    }

    @Override
    public UserDetailProfileByAdminDto readUserProfileByAdmin(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.readAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserDetailProfileByAdminDto(user, dtos);
    }
}
