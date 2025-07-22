package com.tablelog.tablelogback.domain.inquiry.repository;

import com.tablelog.tablelogback.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
