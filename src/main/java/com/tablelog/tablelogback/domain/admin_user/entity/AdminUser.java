package com.tablelog.tablelogback.domain.admin_user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.AdminRequestType;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_ADMIN_USER")
@Entity
public class AdminUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private ApplyStatus status;

    @Column
    private AdminRequestType requestType;

    @Builder
    public AdminUser(Long userId, ApplyStatus status, AdminRequestType requestType){
        this.userId = userId;
        this.status = status;
        this.requestType = requestType;
    }

    public void updateStatus(ApplyStatus status){
        this.status = status;
    }
}
