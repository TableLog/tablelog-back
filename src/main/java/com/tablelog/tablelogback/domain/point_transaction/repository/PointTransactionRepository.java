package com.tablelog.tablelogback.domain.point_transaction.repository;

import com.tablelog.tablelogback.domain.point_transaction.entity.PointTransaction;
import com.tablelog.tablelogback.global.enums.PointType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Slice<PointTransaction> findAllByUserId(Long userId, Pageable pageable);
    Slice<PointTransaction> findAllByUserIdAndPointType(Long userId, PointType pointType, Pageable pageable);
}
