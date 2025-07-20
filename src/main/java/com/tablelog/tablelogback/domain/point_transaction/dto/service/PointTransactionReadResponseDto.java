package com.tablelog.tablelogback.domain.point_transaction.dto.service;

import com.tablelog.tablelogback.global.enums.PointReason;
import com.tablelog.tablelogback.global.enums.PointType;

import java.time.LocalDateTime;

public record PointTransactionReadResponseDto(
        Long id,
        Long userId,
        PointReason pointReason,
        int amount,
        PointType pointType,
        LocalDateTime modifiedAt
) {
}
