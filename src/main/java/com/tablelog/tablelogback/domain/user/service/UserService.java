package com.tablelog.tablelogback.domain.user.service;

import com.tablelog.tablelogback.domain.user.dto.service.request.UpdateUserServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserLoginServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import com.tablelog.tablelogback.domain.user.dto.service.response.UserLoginResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void signUp(UserSignUpServiceRequestDto userSignUpServiceRequestDto, MultipartFile multipartFile) throws IOException;
    UserLoginResponseDto login(UserLoginServiceRequestDto userLoginServiceRequestDto);
    UserLoginResponseDto getUser(String token);
    void updateUser(User user, UpdateUserServiceRequestDto updateUserServiceRequestDto,
                    MultipartFile multipartFile)throws IOException;
    void logout(String token, HttpServletResponse httpServletResponse);
    void deleteUser(User user, HttpServletResponse httpServletResponse);
    UserLoginResponseDto refreshAccessToken(String refreshToken, HttpServletResponse response);
}
