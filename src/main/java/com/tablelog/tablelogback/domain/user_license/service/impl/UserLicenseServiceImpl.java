package com.tablelog.tablelogback.domain.user_license.service.impl;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.entity.UserLicense;
import com.tablelog.tablelogback.domain.user_license.mapper.entity.UserLicenseEntityMapper;
import com.tablelog.tablelogback.domain.user_license.repository.UserLicenseRepository;
import com.tablelog.tablelogback.domain.user_license.service.UserLicenseService;
import com.tablelog.tablelogback.global.s3.S3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class UserLicenseServiceImpl implements UserLicenseService {
    private final UserLicenseRepository userLicenseRepository;
    private final S3Provider s3Provider;
    private final UserLicenseEntityMapper userLicenseEntityMapper;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    private final String SEPARATOR = "/";

    @Override
    public void createUserLicense(UserLicenseCreateServiceRequestDto serviceRequestDto,
                                  User user, MultipartFile multipartFile
    ) throws IOException {
        // 개수 추가
        String fileName;
        String imageUrl = null;
        String folderName = user.getNickname();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = s3Provider.originalFileName(multipartFile);
            imageUrl = url + folderName + SEPARATOR + fileName;
            s3Provider.createFolder(folderName);
            s3Provider.saveFile(multipartFile, folderName + SEPARATOR + fileName);
        }
        UserLicense userLicense = userLicenseEntityMapper.toUserLicense(serviceRequestDto, user.getId(), imageUrl);
        userLicenseRepository.save(userLicense);
    }

    // 사업자 개수 추가, 특허증 개수 추가

}
