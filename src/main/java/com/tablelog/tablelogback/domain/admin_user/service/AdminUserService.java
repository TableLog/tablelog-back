package com.tablelog.tablelogback.domain.admin_user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.dto.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.global.enums.ApplyStatus;

public interface AdminUserService {
    void processPendingWithdrawals();
    void approveDeleteUser(Long id) throws JacksonException;
    AdminUserSliceReadResponseDto getAllAdminUser(ApplyStatus status, int pageNum);
    void approveExpertVerification(Long id);
}
