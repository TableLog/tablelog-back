package com.tablelog.tablelogback.domain.point_transaction.dto.service;

import java.util.List;

public record PointTransactionSliceResponseDto(
        List<PointTransactionReadResponseDto> contents,
        boolean hasNext
) {
}
