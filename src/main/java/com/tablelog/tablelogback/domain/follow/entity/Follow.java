package com.tablelog.tablelogback.domain.follow.entity;

import com.tablelog.tablelogback.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_FOLLOW")
public class Follow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long followerId;

    @Column(nullable = false)
    private Long followingId;

    @Builder
    public Follow(final Long followerId, final Long followingId){
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
