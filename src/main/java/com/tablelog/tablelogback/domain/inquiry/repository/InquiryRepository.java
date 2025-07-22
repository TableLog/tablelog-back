package com.tablelog.tablelogback.domain.inquiry.repository;

import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
//    Slice<Inquiry> findAllApplyStatusAndInquiryType(ApplyStatus applyStatus, InquiryType inquiryType, Pageable pageable);
}
