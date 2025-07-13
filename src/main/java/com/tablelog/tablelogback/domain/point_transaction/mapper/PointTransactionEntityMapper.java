package com.tablelog.tablelogback.domain.point_transaction.mapper;

import com.tablelog.tablelogback.domain.point_transaction.dto.service.PointTransactionReadResponseDto;
import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PointTransactionEntityMapper {
    List<PointTransactionReadResponseDto> toPointTransactionReadAllResponseDto(List<PointTransaction> pointTransactions);
}
