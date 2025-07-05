package com.tablelog.tablelogback.domain.user_license.repository;

import com.tablelog.tablelogback.domain.user_license.entity.UserLicense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLicenseRepository extends JpaRepository<UserLicense, Long> {
}
