package com.tablelog.tablelogback.domain.follow.repository;

import com.tablelog.tablelogback.domain.follow.entity.Follow;
import com.tablelog.tablelogback.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    @Query("SELECT u FROM User u WHERE u.id IN " +
            "(SELECT f.followerId FROM Follow f WHERE f.followingId = :followingId)")
    Slice<User> findAllFollowersByFollowingId(@Param("followingId") Long followingId, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.id IN " +
            "(SELECT f.followingId FROM Follow f WHERE f.followerId = :followerId)")
    Slice<User> findAllFollowingsByFollowerId(@Param("followerId") Long followerId, Pageable pageable);
    @Query("""
    SELECT f.followingId FROM Follow f
    WHERE f.followerId = :meId AND f.followingId IN :targetIds
""")
    List<Long> findAllFollowingIdsByFollowerId(@Param("meId") Long meId, @Param("targetIds") List<Long> targetIds);
    Long countFollowerIdByFollowingId(Long userId);
    Long countFollowingIdByFollowerId(Long userId);
}
