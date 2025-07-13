package com.tablelog.tablelogback.domain.admin_user.service;

import com.fasterxml.jackson.core.JacksonException;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserRejectReasonServiceRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.global.enums.ApplyStatus;

public interface AdminUserService {
    void processPendingWithdrawals();
    void approveDeleteUser(Long id) throws JacksonException;
    AdminUserSliceReadResponseDto getAllAdminUser(ApplyStatus status, int pageNum);
    void reviewExpertVerification(Long id);
    void rejectExpertVerification(Long id, AdminUserRejectReasonServiceRequestDto serviceRequestDto);
}
