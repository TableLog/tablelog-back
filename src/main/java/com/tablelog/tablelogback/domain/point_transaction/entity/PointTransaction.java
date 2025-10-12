package com.tablelog.tablelogback.domain.point_transaction.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.PointReason;
import com.tablelog.tablelogback.global.enums.PointType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_POINT_TRANSACTION")
@Entity
public class PointTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int amount;

    @Column
    private PointReason pointReason;

    @Column(nullable = false)
    private PointType pointType;

    @Builder
    public PointTransaction(final Long userId, final int amount, final PointReason pointReason, final PointType pointType){
        this.userId = userId;
        this.amount = amount;
        this.pointReason = pointReason;
        this.pointType = pointType;
    }
}
