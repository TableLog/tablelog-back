package com.tablelog.tablelogback.domain.report.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.ReportTargetType;
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

    @Column(length = 300)
    private String reportContent;

    @Column
    private ReportTargetType reportTargetType;

    @Column
    private ApplyStatus status;

    @Column(nullable = false)
    private Long targetId;

    @Builder
    public Report(final Long reporterId, final Long reportedUserId,
                  final String reportContent, final ReportTargetType reportTargetType,
                  final Long targetId
    ) {
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reportContent = reportContent;
        this.reportTargetType = reportTargetType;
        this.status = ApplyStatus.APPLIED;
        this.targetId = targetId;
    }

    public void approve(){
        this.status = ApplyStatus.APPROVED;
    }

    public void reject(){
        this.status = ApplyStatus.REJECTED;
    }

    public void processing(){
        this.status = ApplyStatus.PROCESSING;
    }
}
