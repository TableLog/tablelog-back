package com.tablelog.tablelogback.domain.user_license.service.impl;

import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCountResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseReadResponseDto;
import com.tablelog.tablelogback.domain.user_license.dto.service.UserLicenseSliceResponseDto;
import com.tablelog.tablelogback.domain.user_license.entity.UserLicense;
import com.tablelog.tablelogback.domain.user_license.exception.NotFoundUserLicenseException;
import com.tablelog.tablelogback.domain.user_license.exception.UserLicenseErrorCode;
import com.tablelog.tablelogback.domain.user_license.mapper.entity.UserLicenseEntityMapper;
import com.tablelog.tablelogback.domain.user_license.repository.UserLicenseRepository;
import com.tablelog.tablelogback.domain.user_license.service.UserLicenseService;
import com.tablelog.tablelogback.global.enums.LicenseType;
import com.tablelog.tablelogback.global.s3.S3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserLicenseServiceImpl implements UserLicenseService {
    private final UserLicenseRepository userLicenseRepository;
    private final S3Provider s3Provider;
    private final UserLicenseEntityMapper userLicenseEntityMapper;

    @Override
    public void createUserLicense(UserLicenseCreateServiceRequestDto serviceRequestDto, User user) throws IOException {
        UserLicense userLicense = userLicenseEntityMapper.toUserLicense(serviceRequestDto, user.getId());
        userLicenseRepository.save(userLicense);
    }

    @Override
    public UserLicenseSliceResponseDto getAllUserLicenseByUser(int pageNum, User user){
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<UserLicense> slice = userLicenseRepository.findAllByUserId(user.getId(), pageRequest);
        List<UserLicenseReadResponseDto> userLicenses =
                userLicenseEntityMapper.toUserLicenseReadAllResponseDto(slice.getContent());
        return new UserLicenseSliceResponseDto(userLicenses, slice.hasNext());
    }

    @Override
    public UserLicenseSliceResponseDto getAllUserLicensesByUserAndLicenseType(LicenseType licenseType, int pageNum, User user){
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<UserLicense> slice = null;
        if(licenseType == LicenseType.BUSINESS_REGISTRATION) {
            slice = userLicenseRepository.findAllByUserIdAndLicenseType(
                    user.getId(), pageRequest, LicenseType.BUSINESS_REGISTRATION);
        } else if(licenseType == LicenseType.PATENT){
            slice = userLicenseRepository.findAllByUserIdAndLicenseType(
                    user.getId(), pageRequest, LicenseType.PATENT);
        }
        List<UserLicenseReadResponseDto> userLicenses =
                userLicenseEntityMapper.toUserLicenseReadAllResponseDto(slice.getContent());
        return new UserLicenseSliceResponseDto(userLicenses, slice.hasNext());
    }

    @Override
    public UserLicenseCountResponseDto getCountByUser(User user){
        Long recipeCount = user.getRecipeCount();
        Long businessCount = userLicenseRepository
                .countByUserIdAndLicenseType(user.getId(), LicenseType.BUSINESS_REGISTRATION);
        Long patentCount = userLicenseRepository.countByUserIdAndLicenseType(user.getId(), LicenseType.PATENT);
        return userLicenseEntityMapper.toUserLicenseCountResponseDto(user.getId(), recipeCount, businessCount, patentCount);
    }

    @Override
    public UserLicenseSliceResponseDto getAllUserLicenseByUserId(Long userId, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Slice<UserLicense> slice = userLicenseRepository.findAllByUserId(userId, pageRequest);
        List<UserLicenseReadResponseDto> userLicenses =
                userLicenseEntityMapper.toUserLicenseReadAllResponseDto(slice.getContent());
        return new UserLicenseSliceResponseDto(userLicenses, slice.hasNext());
    }

    @Override
    public UserLicenseReadResponseDto getUserLicense(Long id){
        UserLicense userLicense = userLicenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserLicenseException(UserLicenseErrorCode.NOT_FOUND_USER_LICENSE));
        return userLicenseEntityMapper.toUserLicenseReadResponseDto(userLicense);
    }
}
