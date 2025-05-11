package com.tablelog.tablelogback.domain.user.service.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.FindEmailResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.OAuthAccountResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.*;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.OAuthAccountService;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.enums.UserProvider;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import com.tablelog.tablelogback.global.jwt.RefreshToken;
import com.tablelog.tablelogback.global.jwt.RefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.exception.*;
import com.tablelog.tablelogback.global.jwt.oauth2.GoogleRefreshTokenRepository;
import com.tablelog.tablelogback.global.jwt.oauth2.KakaoRefreshTokenRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private final KakaoRefreshTokenRepository kakaoRefreshTokenRepository;
    private final S3Provider s3Provider;
    private final OAuthAccountService oAuthAccountService;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
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
        if (userRepository.existsByEmail(serviceRequestDto.email())) {
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
            fileUrl = url + folderName + SEPARATOR + fileName;
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
        userRepository.save(user);
        return user;
    }

    @Override
    public UserLoginResponseDto login(final UserLoginServiceRequestDto userLoginServiceRequestDto) {
        User user = userRepository.findByEmail(userLoginServiceRequestDto.email())
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        if(!passwordEncoder.matches(userLoginServiceRequestDto.password(),user.getPassword())){
            throw new NotMatchPasswordException(UserErrorCode.NOT_MATCH_PASSWORD);
        }
        jwtUtil.addTokenToCookie(user, httpServletResponse, "accessToken");
        String refresh = jwtUtil.addTokenToCookie(user, httpServletResponse, "refreshToken");
        RefreshToken refreshToken = new RefreshToken(user.getId(), refresh, timeToLive);
        refreshTokenRepository.save(refreshToken);
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
    }

    @Override
    public UserLoginResponseDto getUser(final String token){
        if (jwtUtil.isExpiredAccessToken(token)) {
            throw new ExpiredJwtAccessTokenException(JwtErrorCode.EXPIRED_JWT_ACCESS_TOKEN);
        }
        String email = jwtUtil.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(user.getId());
        return userEntityMapper.toUserLoginResponseDto(user, dtos);
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
            user.updateNickname(serviceRequestDto.nickname());
        }

        // 프로필 이미지
        String imageName = s3Provider.updateImage(user.getProfileImgUrl(), user.getFolderName(), multipartFile);
        user.updateProfileImgUrl(imageName);

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
        jwtUtil.deleteCookie("accessToken", response);
        jwtUtil.deleteCookie("refreshToken", response);
        if(user.getProvider() == UserProvider.kakao){
            jwtUtil.deleteCookie("Kakao-Access-Token", response);
            jwtUtil.deleteCookie("Kakao-Refresh-Token", response);
            kakaoRefreshTokenRepository.deleteById(String.valueOf(user.getId()));
        } else if(user.getProvider() == UserProvider.google){
            jwtUtil.deleteCookie("Google-Access-Token", response);
            jwtUtil.deleteCookie("Google-Refresh-Token", response);
        }
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
    }

    @Transactional
    public void deleteUser(final User user,
                           final HttpServletResponse response
    ) throws JacksonException {
        userRepository.findById(user.getId())
                .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        jwtUtil.deleteCookie("accessToken", response);
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
                                                   final String socialRefreshToken,
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
        jwtUtil.deleteCookie("accessToken", response);
        jwtUtil.addTokenToCookie(user, response, "accessToken");
        jwtUtil.deleteCookie("refreshToken", response);
        String newToken = jwtUtil.addTokenToCookie(user, response, "refreshToken");
        refreshTokenRepository.deleteById(String.valueOf(user.getId()));
        refreshTokenRepository.save(new RefreshToken(user.getId(), newToken, timeToLive));
        List<OAuthAccountResponseDto> dtos = oAuthAccountService.getAllOAuthAccountDtos(user.getId());
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

    @Override
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
}
