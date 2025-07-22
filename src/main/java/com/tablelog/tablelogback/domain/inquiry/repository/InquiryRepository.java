package com.tablelog.tablelogback.domain.inquiry.repository;

import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("""
    SELECT i FROM Inquiry i
    WHERE i.userId = :userId
    AND (:applyStatus IS NULL OR i.applyStatus = :applyStatus)
    AND (:inquiryType IS NULL OR i.inquiryType = :inquiryType)
""")
    Slice<Inquiry> findAllByUserWithOptionalFilters(
            @Param("userId") Long userId,
            @Param("applyStatus") ApplyStatus applyStatus,
            @Param("inquiryType") InquiryType inquiryType,
            Pageable pageable
    );
}
