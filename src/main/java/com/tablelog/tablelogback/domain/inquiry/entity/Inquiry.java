package com.tablelog.tablelogback.domain.inquiry.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.InquiryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_INQUIRY")
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private InquiryType inquiryType;

    @Column
    private String content;

    @Column
    private ApplyStatus applyStatus;

    @Builder
    public Inquiry(final Long userId, final InquiryType inquiryType, final String content){
        this.userId = userId;
        this.inquiryType = inquiryType;
        this.content = content;
        this.applyStatus = ApplyStatus.APPLIED;
    }
}
