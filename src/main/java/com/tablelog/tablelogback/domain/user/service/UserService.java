package com.tablelog.tablelogback.domain.user.service;

import com.tablelog.tablelogback.domain.user.dto.service.request.UserSignUpServiceRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void signUp(UserSignUpServiceRequestDto userSignUpServiceRequestDto, MultipartFile multipartFile) throws IOException;
}
