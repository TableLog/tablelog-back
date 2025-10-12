package com.tablelog.tablelogback.domain.point_transaction.service.impl;

import com.tablelog.tablelogback.domain.point_transaction.dto.service.PointTransactionReadResponseDto;
import com.tablelog.tablelogback.domain.point_transaction.dto.service.PointTransactionSliceResponseDto;
import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.domain.point_transaction.mapper.PointTransactionEntityMapper;
import com.tablelog.tablelogback.domain.point_transaction.repository.PointTransactionRepository;
import com.tablelog.tablelogback.domain.point_transaction.service.PointTransactionService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.global.enums.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PointTransactionServiceImpl implements PointTransactionService {
    private final PointTransactionRepository pointTransactionRepository;
    private final PointTransactionEntityMapper pointTransactionEntityMapper;

    @Override
    public PointTransactionSliceResponseDto getAllPointTransactionByUser(PointType pointType, User user, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, 5);
        Slice<PointTransaction> slice;
        if(pointType == null) {
            slice = pointTransactionRepository.findAllByUserId(user.getId(), pageRequest);
        } else {
            slice = pointTransactionRepository.findAllByUserIdAndPointType(user.getId(), pointType, pageRequest);
        }
        List<PointTransactionReadResponseDto> dtos = pointTransactionEntityMapper
                .toPointTransactionReadAllResponseDto(slice.getContent());
        return new PointTransactionSliceResponseDto(dtos, slice.hasNext());
    }
}
