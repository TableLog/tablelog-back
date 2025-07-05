package com.tablelog.tablelogback.domain.report.repository;

import com.tablelog.tablelogback.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
