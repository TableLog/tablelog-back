package com.tablelog.tablelogback.domain.user.service.impl;

import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.AlreadyExistsEmailException;
import com.tablelog.tablelogback.domain.user.exception.DuplicateNicknameException;
import com.tablelog.tablelogback.domain.user.exception.NotMatchPasswordException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.mapper.entity.UserEntityMapper;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import com.tablelog.tablelogback.domain.user.service.UserService;
import com.tablelog.tablelogback.global.enums.UserRole;
import com.tablelog.tablelogback.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserEntityMapper userEntityMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final JwtUtil jwtUtil;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final RecipeRepository recipeRepository;
//    private final BoardRepository boardRepository;

    private final String SEPARATOR = "/";

    @Override
    public void signUp(final UserSignUpServiceRequestDto serviceRequestDto,
                       MultipartFile multipartFile) throws IOException {
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
            User user = userEntityMapper.toUser(serviceRequestDto, UserRole.USER, fileUrl, serviceRequestDto.nickname());
            userRepository.save(user);
        } else {
//            fileName = s3Provider.originalFileName(multipartFile);
//            fileUrl = url + serviceRequestDto.nickname() + SEPARATOR + fileName;
            fileUrl = multipartFile.getOriginalFilename();
            User user = userEntityMapper.toUser(serviceRequestDto, UserRole.USER, fileUrl, serviceRequestDto.nickname());
            userRepository.save(user);
//            fileUrl = user.getFolderName() + SEPARATOR + fileName;
//            s3Provider.createFolder(serviceRequestDto.email());
//            s3Provider.saveFile(multipartFile, fileUrl);
        }
    }
}
