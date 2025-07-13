package com.tablelog.tablelogback.domain.admin_user.repository;

import com.tablelog.tablelogback.domain.admin_user.entity.AdminUser;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Boolean existsByUserId(Long userId);
    Slice<AdminUser> findAllByStatus(ApplyStatus status, Pageable pageable);
}
