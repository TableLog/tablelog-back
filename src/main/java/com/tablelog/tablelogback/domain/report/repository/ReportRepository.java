package com.tablelog.tablelogback.domain.report.repository;

import com.tablelog.tablelogback.domain.report.entity.Report;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Slice<Report> findAllByStatus(ApplyStatus status, Pageable pageable);
}
