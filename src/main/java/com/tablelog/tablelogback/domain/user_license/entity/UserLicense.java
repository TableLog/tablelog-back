package com.tablelog.tablelogback.domain.user_license.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.ApplyStatus;
import com.tablelog.tablelogback.global.enums.LicenseType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_USER_LICENSE")
public class UserLicense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String licenseName;

    @Column(nullable = false)
    private LicenseType licenseType;

    @Column
    private ApplyStatus status;

    @Builder
    public UserLicense(final Long userId, final String licenseName, final LicenseType licenseType){
        this.userId = userId;
        this.licenseName = licenseName;
        this.licenseType = licenseType;
        this.status = ApplyStatus.APPLIED;
    }
}
