package com.tablelog.tablelogback.domain.user_license.repository;

import com.tablelog.tablelogback.domain.user_license.entity.UserLicense;
import com.tablelog.tablelogback.global.enums.LicenseType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLicenseRepository extends JpaRepository<UserLicense, Long> {
    Slice<UserLicense> findAllByUserId(Long userId, Pageable pageable);
    Slice<UserLicense> findAllByUserIdAndLicenseType(Long userId, Pageable pageable, LicenseType licenseType);
    Long countByUserIdAndLicenseType(Long userId, LicenseType licenseType);
}
