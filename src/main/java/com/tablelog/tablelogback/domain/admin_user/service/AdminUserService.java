package com.tablelog.tablelogback.domain.admin_user.service;

import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserRejectReasonServiceRequestDto;
import com.tablelog.tablelogback.domain.admin_user.dto.service.AdminUserSliceReadResponseDto;
import com.tablelog.tablelogback.global.enums.ApplyStatus;

public interface AdminUserService {
    void processWithdrawals();
//    void approveDeleteUser(Long id) throws JacksonException;
    AdminUserSliceReadResponseDto readAllAdminUser(ApplyStatus status, int pageNum);
    void reviewExpertVerification(Long id);
    void rejectExpertVerification(Long id, AdminUserRejectReasonServiceRequestDto serviceRequestDto);
}
