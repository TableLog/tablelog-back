package com.tablelog.tablelogback.domain.user_license.service;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCountResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseReadResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseSliceResponseDto;
import com.tablelog.tablelogback.global.enums.LicenseType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserLicenseService {
    void createUserLicense(UserLicenseCreateServiceRequestDto serviceRequestDto,
                           MultipartFile multipartFile, User user) throws IOException;
    UserLicenseSliceResponseDto readAllUserLicenseByUser(int pageNum, User user);
    UserLicenseSliceResponseDto readAllUserLicensesByUserAndLicenseType(LicenseType licenseType, int pageNum, User user);
    UserLicenseCountResponseDto readCountByUser(User user);
    UserLicenseSliceResponseDto readAllUserLicenseByUserId(Long userId, int pageNum);
    UserLicenseReadResponseDto readUserLicense(Long id);
}
