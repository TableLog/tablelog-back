package com.tablelog.tablelogback.domain.user.service;

import com.tablelog.tablelogback.domain.follow.dto.FollowUserListDto;
import com.tablelog.tablelogback.domain.user.dto.service.request.*;
import com.tablelog.tablelogback.domain.user.dto.service.response.*;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void checkDuplicate(UserSignUpServiceRequestDto serviceRequestDto);
    User signUp(UserSignUpServiceRequestDto userSignUpServiceRequestDto, MultipartFile multipartFile) throws IOException;
    Boolean login(UserLoginServiceRequestDto userLoginServiceRequestDto);
    UserLoginResponseDto getUser(String token);
    UserProfileDto getUserProfile(Long userId, UserDetailsImpl userDetails);
    FollowUserListDto findUsers(String keyword, int pageNum, UserDetailsImpl userDetails);
    void updateUser(User user, UpdateUserServiceRequestDto updateUserServiceRequestDto,
                    MultipartFile multipartFile, HttpServletResponse httpServletResponse)throws IOException;
    void logout(String token, HttpServletResponse httpServletResponse);
    void deleteUser(User user, HttpServletResponse httpServletResponse);
    UserLoginResponseDto refreshAccessToken(String refreshToken, String socialRefresh, HttpServletResponse response);
    void isNotDupUserEmail(isNotDupUserEmailServiceRequestDto serviceRequestDto);
    void isNotDupUserNick(isNotDupUserNickServiceRequestDto serviceRequestDto);
    void updatePassword(UpdatePasswordServiceRequestDto serviceRequestDto);
    FindEmailResponseDto findEmail(findEmailServiceRequestDto serviceRequestDto);
    void requestExpertVerification(User user);
    UserAllStatisticTypeDto readUserStatistics();
    UserProfileByAdminSliceDto readAllUserProfileByAdmin(String keyword, int pageNum);
    UserDetailProfileByAdminDto readUserProfileByAdmin(Long id);
}
