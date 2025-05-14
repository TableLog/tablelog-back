package com.tablelog.tablelogback.domain.user.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import com.tablelog.tablelogback.global.enums.UserProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_OAUTH_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "email"}))
@Entity
public class OAuthAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UserProvider provider;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public OAuthAccount(UserProvider provider, String email, Long userId) {
        this.provider = provider;
        this.email = email;
        this.userId = userId;
    }
}
