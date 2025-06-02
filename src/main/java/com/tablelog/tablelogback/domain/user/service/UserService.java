package com.tablelog.tablelogback.domain.user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.FindEmailResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserProfileDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void checkDuplicate(UserSignUpServiceRequestDto serviceRequestDto);
    User signUp(UserSignUpServiceRequestDto userSignUpServiceRequestDto, MultipartFile multipartFile) throws IOException;
    UserLoginResponseDto login(UserLoginServiceRequestDto userLoginServiceRequestDto);
    UserLoginResponseDto getUser(String token);
    UserProfileDto getUserProfile(Long userId, UserDetailsImpl userDetails);
    void updateUser(User user, UpdateUserServiceRequestDto updateUserServiceRequestDto,
                    MultipartFile multipartFile, HttpServletResponse httpServletResponse)throws IOException;
    void logout(String token, HttpServletResponse httpServletResponse);
    void deleteUser(User user, HttpServletResponse httpServletResponse) throws JacksonException;
    UserLoginResponseDto refreshAccessToken(String refreshToken, String socialRefresh, HttpServletResponse response);
    void isNotDupUserEmail(isNotDupUserEmailServiceRequestDto serviceRequestDto);
    void isNotDupUserNick(isNotDupUserNickServiceRequestDto serviceRequestDto);
    void updatePassword(UpdatePasswordServiceRequestDto serviceRequestDto);
    FindEmailResponseDto findEmail(findEmailServiceRequestDto serviceRequestDto);
}
