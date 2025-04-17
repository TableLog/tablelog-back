package com.tablelog.tablelogback.domain.user.service.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.GoogleService;
import com.tablelog.tablelogback.domain.user.service.KakaoService;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.exception.*;
import com.tablelog.tablelogback.global.s3.S3Provider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

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
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final S3Provider s3Provider;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";
    @Value("${spring.jwt.refresh.expiration-period}")
    private Long timeToLive;

    @Override
    public void signUp(final UserSignUpServiceRequestDto serviceRequestDto,
                       MultipartFile multipartFile) throws IOException {
        // 이름과 생년월일 중복 체크   
        if(userRepository.existsByNameAndBirthday(serviceRequestDto.name(), serviceRequestDto.birthday())){
            throw new AlreadyExistsUserException(UserErrorCode.ALREADY_EXIST_USER);
        }
        // 이메일 중복 체크
        if (userRepository.existsByEmail(serviceRequestDto.email())) {
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 닉네임 중복 체크
        if(userRepository.existsByNickname(serviceRequestDto.nickname())) {
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        // 비번 확인 체크
        if(!serviceRequestDto.password().equals(serviceRequestDto.confirmPassword())) {
            throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
        }
        // 프로필 이미지 업로드
        String fileName;
        String fileUrl;
        if (multipartFile == null || multipartFile.isEmpty()){
            fileUrl = null;
            User user = userEntityMapper.toUser(serviceRequestDto, UserRole.NORMAL,
                    fileUrl, serviceRequestDto.nickname());
            userRepository.save(user);
        } else {
            fileName = s3Provider.originalFileName(multipartFile);
            fileUrl = url + serviceRequestDto.nickname() + SEPARATOR + fileName;
            User user = userEntityMapper.toUser(serviceRequestDto, UserRole.NORMAL,
                    fileUrl, serviceRequestDto.nickname());
            userRepository.save(user);
            fileUrl = user.getFolderName() + SEPARATOR + fileName;
            s3Provider.createFolder(serviceRequestDto.email());
            s3Provider.saveFile(multipartFile, fileUrl);
        }
    }

    @Override
    public UserLoginResponseDto login(final UserLoginServiceRequestDto userLoginServiceRequestDto) {
        User user = userRepository.findByEmail(userLoginServiceRequestDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(!passwordEncoder.matches(userLoginServiceRequestDto.password(),user.getPassword())){
            throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
        }
        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
        String refresh = jwtUtil.addRefreshTokenToCookie(user, httpServletResponse);
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    @Override
    public UserLoginResponseDto getUser(final String token){
        if (jwtUtil.isExpiredAccessToken(token)) {
            throw new ExpiredJwtAccessTokenException(JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
        }
        String email = jwtUtil.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    @Transactional
    public void updateUser(User user,
                           UpdateUserServiceRequestDto serviceRequestDto,
                           MultipartFile multipartFile) throws IOException {
        // 소셜 미연동 시
        if(user.getKakaoEmail() != null || user.getGoogleEmail() != null){
            // 이메일
            if(!Objects.equals(serviceRequestDto.newPassword(), "")){
                if(userRepository.existsByEmail(serviceRequestDto.newEmail())){
                    throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
                }
                user.updateEmail(serviceRequestDto.newEmail());
            }
            // 비밀번호
            if(!Objects.equals(serviceRequestDto.newPassword(), "")){
                if (passwordEncoder.matches(serviceRequestDto.newPassword(), user.getPassword())) {
                    throw new NotMatchPasswordException(UserErrorCode.MATCH_CURRENT_PASSWORD);
                }
                if (!serviceRequestDto.newPassword().equals(serviceRequestDto.confirmNewPassword())) {
                    throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
                }
                user.updatePassword(passwordEncoder.encode(serviceRequestDto.newPassword()));
            }
        }

        // 닉네임
        if(!Objects.equals(serviceRequestDto.nickname(), "")){
            if(userRepository.existsByNickname(serviceRequestDto.nickname())){
                throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
            }
            user.updateNickname(serviceRequestDto.nickname());
        }

        // 프로필 이미지
        if(serviceRequestDto.imageChange()) {
            String imageName;
            if(multipartFile == null || multipartFile.isEmpty()){
                imageName = s3Provider.updateImage(user.getProfileImgUrl(), user.getFolderName(), multipartFile);
                s3Provider.delete(imageName);
                user.updateProfileImgUrl(null);
            }
            else {
                imageName = s3Provider.updateImage(user.getProfileImgUrl(), user.getFolderName(), multipartFile);
                user.updateProfileImgUrl(imageName);
            }
        }

        userRepository.save(user);
    }

    @Override
    public void logout(final String token, final HttpServletResponse response){
        if (jwtUtil.isExpiredAccessToken(token)) {
            throw new ExpiredJwtAccessTokenException(JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
        }
        String email = jwtUtil.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        jwtUtil.deleteCookie("refreshToken", response);
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
    }

    @Transactional
    public void deleteUser(final User user, final String kakaoAccessToken,
                           final String googleAccessToken,
                           final HttpServletResponse response
    ) throws JacksonException {
        userRepository.findById(user.getId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(user.getKakaoEmail() != null) {
            if(kakaoAccessToken == null || kakaoAccessToken.equals("")){
                throw new FailedUnlinkKakaoException(UserErrorCode.FAILED_UNLINK_KAKAO);
            }
            kakaoService.unlinkKakao(kakaoAccessToken);
        }
        if(user.getGoogleEmail() != null) {
            if(googleAccessToken == null || googleAccessToken.equals("")){
                throw new FailedUnlinkGoogleException(UserErrorCode.FAILED_UNLINK_GOOGLE);
            }
            googleService.unlinkGoogle(googleAccessToken);
        }
        jwtUtil.expireAccessTokenToHeader(user, response);
        jwtUtil.deleteCookie("refreshToken", response);
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));

        if (user.getProfileImgUrl() == null){
            userRepository.deleteById(user.getId());
        } else {
            String image_name = user.getProfileImgUrl().replace(url,"");
            image_name = image_name.substring(image_name.lastIndexOf("/"));
            userRepository.deleteById(user.getId());
            s3Provider.delete(user.getFolderName() + image_name);
        }
    }

    @Override
    public UserLoginResponseDto refreshAccessToken(final String refreshTokenCookie,
                                                   final String kakaoRefreshToken,
                                                   final String googleRefreshToken,
                                                   final HttpServletResponse response) {
        String refresh = refreshTokenCookie;
        if (refreshTokenCookie.startsWith("refreshToken=")) {
            refresh = refreshTokenCookie.substring("refreshToken=".length());
        }
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refresh)
                .orElseThrow(() -> new ExpiredJwtRefreshTokenException(JwtErrorCode.EXPIRED_JWT_REFRESH_TOKEN));
        if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
            throw new FailedJwtTokenException(JwtErrorCode.FAILED_JWT_TOKEN);
        }
        User user = userRepository.findById(refreshToken.getId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));

        // accessToken과 refreshToken 둘 다 refresh
        jwtUtil.addAccessTokenToHeader(user, httpServletResponse);
        jwtUtil.deleteCookie("refreshToken", response);
        String newToken = jwtUtil.addRefreshTokenToCookie(user, response);
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
        refreshTokenRepository.save(new RefreshToken(user.getId(), newToken, timeToLive));
        // 카카오
        if(user.getKakaoEmail() != null){
            try {
                kakaoService.refresh(kakaoRefreshToken, user);
            } catch (Exception e){
                throw new FailedRefreshKakaoException(UserErrorCode.FAILED_REFRESH_KAKAO);
            }
        }
        // 구글
        if(user.getGoogleEmail() != null){
            try {
                googleService.refresh(googleRefreshToken, user);
            } catch (Exception e){
                throw new FailedRefreshGoogleException(UserErrorCode.FAILED_REFRESH_GOOGLE);
            }
        }
        return userEntityMapper.toUserLoginResponseDto(user);
    }

    @Override
    public void isNotDupUserEmail(isNotDupUserEmailServiceRequestDto serviceRequestDto) {
        if (userRepository.existsByEmail(serviceRequestDto.email())) {
            log.info("중복된 이메일입니다.");
            throw new AlreadyExistsEmailException(UserErrorCode.ALREADY_EXIST_EMAIL);
        }
    }

    @Override
    public void isNotDupUserNick(isNotDupUserNickServiceRequestDto serviceRequestDto){
        if(userRepository.existsByNickname(serviceRequestDto.nickname())) {
            log.info("중복된 닉네임입니다.");
            throw new DuplicateNicknameException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Override
    public void updatePassword(User user, UpdatePasswordServiceRequestDto serviceRequestDto){
        if(!Objects.equals(serviceRequestDto.newPassword(), "")){
            if (passwordEncoder.matches(serviceRequestDto.newPassword(), user.getPassword())) {
                throw new NotMatchPasswordException(UserErrorCode.MATCH_CURRENT_PASSWORD);
            }
            if (!serviceRequestDto.newPassword().equals(serviceRequestDto.confirmNewPassword())) {
                throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
            }
            user.updatePassword(passwordEncoder.encode(serviceRequestDto.newPassword()));
        }
    }

    @Override
    public String findEmail(findEmailServiceRequestDto serviceRequestDto){
        User user = userRepository.findByNameAndBirthday(serviceRequestDto.name(), serviceRequestDto.birthday())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        return user.getEmail();
    }
}
