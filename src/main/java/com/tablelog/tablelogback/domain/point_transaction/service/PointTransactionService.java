package com.tablelog.tablelogback.domain.point_transaction.service;

import com.tablelog.tablelogback.domain.point_transaction.dto.service.PointTransactionSliceResponseDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.PointType;

public interface PointTransactionService {
    PointTransactionSliceResponseDto getAllPointTransactionByUser(PointType pointType, User user, int pageNumber);
}
