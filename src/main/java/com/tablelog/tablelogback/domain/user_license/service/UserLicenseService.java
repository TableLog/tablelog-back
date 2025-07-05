package com.tablelog.tablelogback.domain.user_license.service;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserLicenseService {
    void createUserLicense(UserLicenseCreateServiceRequestDto serviceRequestDto,
                           User user, MultipartFile multipartFile) throws IOException;
}
