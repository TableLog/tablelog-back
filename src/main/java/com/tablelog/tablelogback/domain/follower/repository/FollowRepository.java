package com.tablelog.tablelogback.domain.follower.repository;

import com.tablelog.tablelogback.domain.follower.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
