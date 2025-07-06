package com.tablelog.tablelogback.domain.admin_user.service;

import com.fasterxml.jackson.core.JacksonException;

public interface AdminUserService {
    void processPendingWithdrawals();
    void approveDeleteUser(Long id) throws JacksonException;
}
