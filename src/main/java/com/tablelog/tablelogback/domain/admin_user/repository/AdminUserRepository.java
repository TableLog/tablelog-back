package com.tablelog.tablelogback.domain.admin_user.repository;

import com.tablelog.tablelogback.domain.admin_user.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Boolean existsByUserId(Long userId);
}
