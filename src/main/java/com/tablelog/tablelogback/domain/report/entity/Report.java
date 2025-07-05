package com.tablelog.tablelogback.domain.report.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.ReportType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_REPORT")
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporterId; // 신고한 사람

    @Column(nullable = false) // 신고된 사람
    private Long reportedUserId;

    @Column(length = 900) // 300글자
    private String reportContent;

    @Column
    private ReportType reportType;

    @Column
    private ApplyStatus status;

    @Builder
    public Report(Long reporterId, Long reportedUserId, String reportContent, ReportType reportType){
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reportContent = reportContent;
        this.reportType = reportType;
        this.status = ApplyStatus.APPLIED;
    }
}
