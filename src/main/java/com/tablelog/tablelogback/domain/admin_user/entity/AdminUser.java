package com.tablelog.tablelogback.domain.admin_user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.RequestStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue
    private Long id;

    private Long userId;

    private RequestStatus status;

    @Builder
    public AdminUser(Long userId, RequestStatus status){
        this.userId = userId;
        this.status = status;
    }

    public void updateStatus(RequestStatus status){
        this.status = status;
    }
}
